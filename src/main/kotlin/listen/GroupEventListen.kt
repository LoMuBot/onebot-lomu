package cn.luorenmu.listen

import cn.luorenmu.action.OneBotChatStudy
import cn.luorenmu.action.OneBotCommandAllocator
import cn.luorenmu.action.OneBotKeywordReply
import cn.luorenmu.action.listenProcess.BilibiliEventListen
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.entiy.ConfigGroup
import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.repository.GroupMessageRepository
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.GroupMessage
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.annotation.GroupMessageHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


/**
 * @author LoMu
 * Date 2024.07.04 10:22
 */

val groupMessageQueue: RecentlyMessageQueue<GroupMessage> = RecentlyMessageQueue()
val log = KotlinLogging.logger { }

@Component
@Shiro
class GroupEventListen(
    private val groupMessageRepository: GroupMessageRepository,
    private val oneBotCommandAllocator: OneBotCommandAllocator,
    private val oneBotKeywordReply: OneBotKeywordReply,
    private val oneBotChatStudy: OneBotChatStudy,
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val bilibiliEventListen: BilibiliEventListen,
) {


    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val senderId = groupMessageEvent.sender.userId
        val groupId = groupMessageEvent.groupId
        // 替换掉群备注 [CQ:at,qq=141412312,name=群最帅] -> [CQ:at,qq=141412312)
        val message = groupMessageEvent.message.replace(Regex("""(\[CQ:at,qq=\d+),name=[^,\]]*"""), "$1")

        // save data
        val groupMessage =
            GroupMessage(
                null,
                groupId,
                bot.selfId,
                LocalDateTime.now(),
                groupMessageEvent
            )
        groupMessageRepository.save(groupMessage)

        // 禁止回复的群
        val banKeywordList = redisTemplate.opsForValue()["banKeywordGroup"]?.to<ConfigGroup>() ?: run {
            val list = oneBotConfigRepository.findAllByConfigName("banKeywordGroup").map { it.configContent.toLong() }
            val configGroup = ConfigGroup(list)
            redisTemplate.opsForValue()["banKeywordGroup", configGroup.toJSONString(), 1] = TimeUnit.DAYS
            configGroup
        }
        // 关键词消息
        banKeywordList.list.firstOrNull { it == groupId } ?: run {
            oneBotKeywordReply.process(bot, groupMessageEvent.messageId, senderId, groupId, message)
        }

        bilibiliEventListen.process(message)?.let {
            bot.sendGroupMsgLimit(groupId, it)
        }

        val command = message.replace(" ", "")
        // 指令
        try {
            val process =
                oneBotCommandAllocator.process(bot.selfId, command, groupMessageEvent.groupId, groupMessageEvent.sender)
            if (process.isNotBlank()) {
                bot.sendGroupMsg(
                    groupId,
                    MsgUtils.builder().reply(groupMessageEvent.messageId).text(process).build(),
                    false
                )
            }
        } catch (e: Exception) {
            bot.sendGroupMsgLimit(groupId, "服务器内部错误 请求的任务被迫中断")
            throw e
        }


        // 禁止该群学习
        val banStudyList = redisTemplate.opsForValue()["banStudy"].to<ConfigGroup>() ?: run {
            val list = oneBotConfigRepository.findAllByConfigName("banStudy").map { it.configContent.toLong() }
            val configGroup = ConfigGroup(list)
            redisTemplate.opsForValue()["banStudy", configGroup.toJSONString(), 1] = TimeUnit.DAYS
            configGroup
        }
        banStudyList.list.firstOrNull { it == groupId } ?: run {
            oneBotChatStudy.process(bot, groupMessageEvent)
        }


        // 同一个人在指定的20条中发了同一条消息 不入队列
        groupMessageQueue.map[groupId]?.let {
            for (gM in it) {
                if (gM.groupEventObject.message == message && senderId == gM.groupEventObject.sender.userId) {
                    return
                }
            }
        }
        //消息入队
        groupMessageQueue.addMessageToQueue(groupId, groupMessage)
    }
}
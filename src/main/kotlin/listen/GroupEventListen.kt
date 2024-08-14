package cn.luorenmu.listen

import cn.luorenmu.action.OneBotChatStudy
import cn.luorenmu.action.OneBotCommandAllocator
import cn.luorenmu.action.OneBotKeywordReply
import cn.luorenmu.common.extensions.sendGroupMsgKeywordLimit
import cn.luorenmu.dto.RecentlyMessageQueue
import cn.luorenmu.entiy.ConfigGroup
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
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random


/**
 * @author LoMu
 * Date 2024.07.04 10:22
 */

val groupMessageQueue: RecentlyMessageQueue<GroupMessage> = RecentlyMessageQueue()

@Component
@Shiro
class GroupEventListen(
    private val groupMessageRepository: GroupMessageRepository,
    private val oneBotCommandAllocator: OneBotCommandAllocator,
    private val oneBotKeywordReply: OneBotKeywordReply,
    private val oneBotChatStudy: OneBotChatStudy,
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {


    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val senderId = groupMessageEvent.sender.userId
        val groupId = groupMessageEvent.groupId
        val message = groupMessageEvent.message

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
            // 概率回复 40%
            if (Random(System.currentTimeMillis()).nextInt(0, 10) <= 4) {
                val mongodbKeyword = oneBotKeywordReply.process(bot.selfId, senderId, message)
                mongodbKeyword?.let {
                    bot.sendGroupMsgKeywordLimit(groupId, it)
                }
            }
        }


        // bot指令
        val oneBotCommand = message.replace(MsgUtils.builder().at(bot.selfId).build(), "")




        // 外部指令
        val command = oneBotCommand.replace(" ", "")
        val process =
            oneBotCommandAllocator.process(command, senderId, groupMessageEvent.messageId)
        if (process.isNotBlank()) {
            bot.sendGroupMsg(
                groupId,
                MsgUtils.builder().reply(groupMessageEvent.messageId).text(process).build(),
                false
            )
        }


        // 总有没活的人硬整活 禁止该群学习奇怪的东西！
        val banStudyList = redisTemplate.opsForValue()["banStudy"].to<ConfigGroup>() ?: run {
            val list = oneBotConfigRepository.findAllByConfigName("banStudy").map { it.configContent.toLong() }
            val configGroup = ConfigGroup(list)
            redisTemplate.opsForValue()["banStudy", configGroup.toJSONString(), 1] = TimeUnit.DAYS
            configGroup
        }
        banStudyList.list.firstOrNull { it == groupId } ?: run {
            oneBotChatStudy.reReadStudy(bot, groupMessageEvent)
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
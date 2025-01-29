package cn.luorenmu.listen

import cn.luorenmu.action.OneBotCommandAllocator
import cn.luorenmu.action.PermissionsManager
import cn.luorenmu.action.disuse.OneBotChatStudy
import cn.luorenmu.action.disuse.OneBotKeywordReply
import cn.luorenmu.action.listenProcess.BilibiliEventListen
import cn.luorenmu.action.listenProcess.DeerListen
import cn.luorenmu.entiy.ConfigId
import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.listen.entity.MessageType
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
import org.springframework.data.redis.core.StringRedisTemplate
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
    private val oneBotCommandAllocator: OneBotCommandAllocator,
    private val oneBotChatStudy: OneBotChatStudy,
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val redisTemplate: StringRedisTemplate,
    private val keywordReply: OneBotKeywordReply,
    private val bilibiliEventListen: BilibiliEventListen,
    private val deerListen: DeerListen,
    private val permissionsManager: PermissionsManager,
) {

    /**
     * false if the id exist else true not exist
     */
    fun idIsNotExistFunction(configName: String, id: Long): Boolean =
        (redisTemplate.opsForValue()[configName]?.to<ConfigId>()
            ?: run {
                val list =
                    oneBotConfigRepository.findAllByConfigName(configName).map { it.configContent.toLong() }
                val configId = ConfigId(list)
                redisTemplate.opsForValue()[configName, configId.toJSONString(), 1] = TimeUnit.DAYS
                configId
            }).list.firstOrNull { it == id } == null


    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val groupId = groupMessageEvent.groupId
        val sender = groupMessageEvent.sender
        val senderId = sender.userId
        // 替换掉群备注 [CQ:at,qq=141412312,name=群最帅] -> [CQ:at,qq=141412312)
        val message = groupMessageEvent.message.replace(Regex("""(\[CQ:at,qq=\d+),name=[^,\]]*"""), "$1")


        val messageSender = MessageSender(
            groupId,
            sender.nickname,
            senderId,
            permissionsManager.botRole(senderId, sender.role),
            groupMessageEvent.messageId,
            message,
            MessageType.GROUP,
            bot.selfId
        )


        val groupMessage =
            GroupMessage(
                null,
                groupId,
                bot.selfId,
                LocalDateTime.now(),
                groupMessageEvent
            )


        // 指令
        try {
            oneBotCommandAllocator.process(bot, messageSender)?.let {
                if (it.isNotBlank()) {
                    bot.sendGroupMsg(
                        groupId,
                        MsgUtils.builder().reply(groupMessageEvent.messageId).text(it).build(),
                        false
                    )
                }
            }
        } catch (e: Exception) {
            bot.sendGroupMsg(
                groupId,
                MsgUtils.builder().reply(groupMessageEvent.messageId)
                    .text("服务器内部错误 请求的任务被迫中断:${e.message}").build(),
                false
            )
            log.error { "意外错误: ${e.stackTraceToString()} , 因: ${e.message}" }
        }


        // 监听类
        oneBotChatStudy.process(bot, groupMessageEvent)
        keywordReply.process(bot, messageSender)
        bilibiliEventListen.process(bot, messageSender)
        // strange function ?
        deerListen.process(bot, messageSender)


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
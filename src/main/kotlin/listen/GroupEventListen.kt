package cn.luorenmu.listen

import cn.luorenmu.commom.extensions.sendGroupMsgLimit
import cn.luorenmu.dto.RecentlyMessageQueue
import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.GroupMessageRepository
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.entiy.ActiveMessage
import cn.luorenmu.repository.entiy.GroupMessage
import cn.luorenmu.service.OneBotCommandHandle
import cn.luorenmu.service.OneBotKeywordReply
import com.mikuac.shiro.annotation.GroupMessageHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime


/**
 * @author LoMu
 * Date 2024.07.04 10:22
 */


@Component
@Shiro
class GroupEventListen(
    private var groupMessageRepository: GroupMessageRepository,
    private var commandHandler: OneBotCommandHandle,
    private var keywordReply: OneBotKeywordReply,
    private var activeSendMessageRepository: ActiveSendMessageRepository,
    private var keywordReplyRepository: KeywordReplyRepository,
) {
    private val groupMessageQueue: RecentlyMessageQueue<GroupMessage> = RecentlyMessageQueue()


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


        // command
        val command = message.replace(MsgUtils.builder().at(bot.selfId).build(), "")
        if (commandHandler.isCommand(command)) {
            val process =
                commandHandler.process(command, senderId, groupMessageEvent.messageId)
            if (process.isNotBlank()) {
                bot.sendGroupMsg(groupId, process, false)
                return
            }

        }

        // keywordMessage
        val mongodbKeyword = keywordReply.process(bot.selfId, senderId, message)
        if (mongodbKeyword.isNotEmpty()) {
            for (s in mongodbKeyword) {
                bot.sendGroupMsgLimit(groupId, s)
            }
        }

        // reRead
        val lastMessages = groupMessageQueue.lastMessages(groupId, 1)
        if (lastMessages.isNotEmpty()) {
            var repeatNum = 0
            lastMessages.forEach {
                val userId = it?.groupEventObject?.sender?.userId
                if (userId != senderId && it?.groupEventObject?.message == message) {
                    repeatNum++
                }
            }
            if (repeatNum == lastMessages.size) {
                bot.sendGroupMsgLimit(groupId, message)
                activeSendMessageRepository.insert(ActiveMessage(null, -1L, message, null))
            }
        }

        groupMessageQueue.addMessageToQueue(groupId, groupMessage)
    }
}
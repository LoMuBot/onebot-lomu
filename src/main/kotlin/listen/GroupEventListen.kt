package cn.luorenmu.listen

import cn.luorenmu.action.OneBotCommandAllocator
import cn.luorenmu.action.OneBotChatStudy
import cn.luorenmu.action.OneBotKeywordReply
import cn.luorenmu.common.extensions.sendGroupMsgKeywordLimit
import cn.luorenmu.dto.RecentlyMessageQueue
import cn.luorenmu.repository.GroupMessageRepository
import cn.luorenmu.repository.OneBotConfigRespository
import cn.luorenmu.repository.entiy.GroupMessage
import com.mikuac.shiro.annotation.GroupMessageHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime


/**
 * @author LoMu
 * Date 2024.07.04 10:22
 */

val groupMessageQueue: RecentlyMessageQueue<GroupMessage> = RecentlyMessageQueue()

@Component
@Shiro
class GroupEventListen(
    private val groupMessageRepository: GroupMessageRepository,
    private val commandHandler: OneBotCommandAllocator,
    private val oneBotKeywordReply: OneBotKeywordReply,
    private val oneBotCommonProcess: OneBotChatStudy,
    private val oneBotConfigRespository: OneBotConfigRespository,
    private val redisTemplate: RedisTemplate<String, String>
) {


    @GroupMessageHandler
    fun groupMsgListen(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val senderId = groupMessageEvent.sender.userId
        val groupId = groupMessageEvent.groupId
        val message = groupMessageEvent.message

        oneBotConfigRespository.findOneByConfigName(groupMessageEvent.sender.userId.toString())?.let {
            return
        }

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

        // keywordMessage
        val banList = oneBotConfigRespository.findAllByConfigName("banKeywordGroup").map { it.configContent.toLong() }
        var ban = false
        for (oneBotConfig in banList) {
            if (banList.contains(groupId)) {
                ban = true
            }
        }
        if (!ban) {
            val mongodbKeyword = oneBotKeywordReply.process(bot.selfId, senderId, message)
            mongodbKeyword?.let {
                bot.sendGroupMsgKeywordLimit(groupId, it)
            }
        }


        // command
        val command = message.replace(MsgUtils.builder().at(bot.selfId).build(), "").replace(" ", "")
        if (commandHandler.isCommand(command)) {
            val process =
                commandHandler.process(command, senderId, groupMessageEvent.messageId)
            if (process.isNotBlank()) {
                bot.sendGroupMsg(
                    groupId,
                    MsgUtils.builder().reply(groupMessageEvent.messageId).text(process).build(),
                    false
                )
                return
            }
        }

        //reRead
        oneBotConfigRespository.findOneByConfigName("banReRead") ?: run {
            oneBotCommonProcess.reRead(bot, groupMessageEvent)
        }
        groupMessageQueue.map[groupId]?.let{
            for (gM in it) {
                if (gM.groupEventObject.message == message && senderId == gM.groupEventObject.sender.userId) {
                    return
                }
            }
        }
        groupMessageQueue.addMessageToQueue(groupId, groupMessage)
    }
}
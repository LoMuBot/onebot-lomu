package cn.luorenmu.listen

import cn.luorenmu.action.PermissionsManager
import cn.luorenmu.action.commandProcess.OneBotCommandAllocator
import cn.luorenmu.action.listenProcess.BilibiliEventListen
import cn.luorenmu.action.listenProcess.PetpetListen
import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.listen.entity.MessageType
import cn.luorenmu.repository.entiy.GroupMessage
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
    private val oneBotCommandAllocator: OneBotCommandAllocator,
    private val bilibiliEventListen: BilibiliEventListen,
    private val permissionsManager: PermissionsManager,
    private val petpetListen: PetpetListen,
) {
    companion object {
        val groupMessageQueue: RecentlyMessageQueue<GroupMessage> = RecentlyMessageQueue()
    }

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
        oneBotCommandAllocator.process(bot, messageSender)
        // 监听类
        bilibiliEventListen.process(bot, messageSender)
        petpetListen.process(messageSender)


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
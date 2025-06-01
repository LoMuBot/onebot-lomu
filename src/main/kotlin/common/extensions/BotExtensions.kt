package cn.luorenmu.common.extensions

import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.entiy.SelfSendMsg
import cn.luorenmu.listen.entity.MessageType
import cn.luorenmu.repository.entiy.DeepMessage
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.core.BotContainer
import com.mikuac.shiro.dto.action.common.ActionData
import com.mikuac.shiro.dto.action.common.MsgId
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.28 18:03
 */

private val selfRecentlySendMessage: RecentlyMessageQueue<SelfSendMsg> = RecentlyMessageQueue(40)
private val log = KotlinLogging.logger { }

/**
 *  bool 表示消息队列是否存在该消息
 */
fun selfRecentlySent(id: Long, message: String): Boolean {
    synchronized(selfRecentlySendMessage) {
        val selfSendMsgs = selfRecentlySendMessage.map[id]
        selfSendMsgs?.forEach {
            if (it.message == message) {
                return true
            }
        }
        return false
    }
}

fun BotContainer.getFirstBot(): Bot {
    return this.robots.values.first()
}

fun Bot.sendMsg(msgType: MessageType, id: Long, msg: String) {
    when (msgType) {
        MessageType.PRIVATE -> {
            this.sendPrivateMsg(id, msg)
        }

        MessageType.GROUP -> {
            this.sendGroupMsg(id, msg)
        }
    }
}


fun Bot.sendGroupMsgLimit(groupId: Long, message: String) {
    sendMsgLimit(groupId, message, MessageType.GROUP)
}

fun Bot.sendPrivateMsgLimit(id: Long, message: String) {
    sendMsgLimit(id, message, MessageType.PRIVATE)
}


fun Bot.addMsgLimit(id: Long, message: String) {
    synchronized(selfRecentlySendMessage) {

        if (selfRecentlySent(id, message)) {
            return
        }

        selfRecentlySendMessage.addMessageToQueue(id, SelfSendMsg(message))
    }
}

/**
 * msgLimit 用于作为限制的消息  (图片消息)
 * 由于图片的名称一样 但是URL不一样 所以需要截取名称作为限制条件
 */
private fun Bot.sendMsgLimit(
    id: Long,
    message: String,
    messageType: MessageType,
): Boolean {
    synchronized(selfRecentlySendMessage) {
        if (selfRecentlySent(id, message)) {
            return false
        }
        when (messageType) {
            MessageType.PRIVATE -> {
                sendPrivateMsg(id, message)
            }

            MessageType.GROUP -> {
                sendGroupMsg(id, message)
            }
        }
        return true
    }
}

/**
 *  添加日志记录 发送群消息
 */
fun Bot.sendGroupMsg(groupId: Long, message: String) {
    log.info { "send message $groupId -> $message" }
    val sendGroupMsg = this.sendGroupMsg(groupId, message, false)
    addToSelfSendMessage(groupId, message, sendGroupMsg)

}

private fun Bot.addToSelfSendMessage(
    id: Long,
    message: String,
    sendMsg: ActionData<MsgId>?,
) {
    val selfSendMsg: SelfSendMsg = if (sendMsg != null && sendMsg.data != null) {
        SelfSendMsg(sendMsg.data.messageId.toLong(), message)
    } else {
        log.warn { "发送至${id}-${message}返回为null" }
        SelfSendMsg(message)
    }
    selfRecentlySendMessage.addMessageToQueue(id, selfSendMsg)
}

/**
 *  添加日志记录 发送私聊消息
 */
fun Bot.sendPrivateMsg(id: Long, message: String) {
    log.info { "send message $id -> $message" }
    val sendPrivateMsg = this.sendPrivateMsg(id, message, false)
    addToSelfSendMessage(id, message, sendPrivateMsg)
}


fun Bot.sendGroupDeepMsgLimit(groupId: Long, message: String, deepMessage: DeepMessage?): Boolean {
    synchronized(selfRecentlySendMessage) {
        val selfSendMsgs = selfRecentlySendMessage.map[groupId]
        val message1 = message + deepMessage?.reply
        var deepMessage1 = deepMessage
        selfSendMsgs?.forEach {
            if (it.message == message1) {
                return false
            }
        }
        this.sendGroupMsg(groupId, message)

        // 连续发送多条消息
        deepMessage1?.let {
            while (true) {
                TimeUnit.SECONDS.sleep(1)
                this.sendGroupMsg(groupId, it.reply)
                it.next?.let { it1 ->
                    deepMessage1 = it1
                } ?: break
            }
        }
        return true
    }
}


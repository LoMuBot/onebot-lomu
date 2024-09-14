package cn.luorenmu.common.extensions

import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.entiy.SelfSendMsg
import cn.luorenmu.repository.entiy.DeepMessage
import cn.luorenmu.repository.entiy.KeywordReply
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.action.common.ActionData
import com.mikuac.shiro.dto.action.common.MsgId
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.28 18:03
 */

private val selfRecentlySendMessage: RecentlyMessageQueue<SelfSendMsg> = RecentlyMessageQueue(40)
private val selfKeywordMessage: RecentlyMessageQueue<KeywordReply> = RecentlyMessageQueue(30)
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

fun Bot.sendGroupMsgLimit(groupId: Long, message: String): Boolean {
    return sendMsgLimit(groupId, message) {
        this.sendGroupMsg(groupId, message, false)
    }
}

fun Bot.sendGroupMsgLimit(groupId: Long, message: String, msgLimit: String): Boolean {
    return sendMsgLimit(groupId, message, msgLimit) {
        this.sendGroupMsg(groupId, message, false)
    }
}

fun Bot.sendPrivateMsgLimit(id: Long, message: String) {
    sendMsgLimit(id, message) {
        this.sendPrivateMsg(id, message, false)
    }
}

fun Bot.sendGroupMsgKeywordLimit(id: Long, keywordReply: KeywordReply): Boolean {
    var send = false
    synchronized(selfKeywordMessage) {
        selfKeywordMessage.map[id]?.let {
            for (kw in it) {
                if ((keywordReply == kw || keywordReply.keyword == kw.keyword || keywordReply.reply == kw.reply)) {
                    return false
                }
            }
        } ?: run {
            send = true
        }
        if (!selfRecentlySent(id, keywordReply.reply.replaceCqToFileStr() ?: keywordReply.reply)) {
            send = true
        }

        if (send) {
            selfKeywordMessage.addMessageToQueue(id, keywordReply)
            val sendList: ArrayList<String> = arrayListOf()
            sendList.add(keywordReply.reply)
            keywordReply.deepMessage(sendList, keywordReply.nextMessage)
            for (s in sendList) {
                sendGroupMsgLimit(id, s, s.replaceCqToFileStr() ?: "none")
                TimeUnit.SECONDS.sleep(1)
            }
        }
    }
    return send
}

fun Bot.addMsgLimit(id: Long, message: String, msgLimit: String = "none") {
    synchronized(selfRecentlySendMessage) {
        var msgLimit1 = message
        if (msgLimit != "none") {
            msgLimit1 = msgLimit
        }
        if (selfRecentlySent(id, msgLimit1)) {
            return
        }

        selfRecentlySendMessage.addMessageToQueue(id, SelfSendMsg(msgLimit1))
    }
}

/**
 * msgLimit 用于作为限制的消息  (图片消息)
 * 由于图片的名称一样 但是URL不一样 所以需要截取名称作为限制条件
 */
private fun Bot.sendMsgLimit(
    id: Long,
    message: String,
    msgLimit: String = "none",
    send: () -> ActionData<MsgId>?,
): Boolean {
    synchronized(selfRecentlySendMessage) {
        var msgLimit1 = message
        if (msgLimit != "none") {
            msgLimit1 = msgLimit
        }
        if (selfRecentlySent(id, msgLimit1)) {
            return false
        }
        val sendMsg = send()
        val selfSendMsg: SelfSendMsg = if (sendMsg != null && sendMsg.data != null) {
            SelfSendMsg(sendMsg.data.messageId.toLong(), msgLimit1)
        } else {
            SelfSendMsg(msgLimit1)
        }
        selfRecentlySendMessage.addMessageToQueue(id, selfSendMsg)
        log.info { "send message $id -> $message" }
        return true
    }
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
        val sendGroupMsg = this.sendGroupMsg(groupId, message, false)

        // 连续发送多条消息
        deepMessage1?.let {
            while (true) {
                TimeUnit.SECONDS.sleep(1)
                this.sendGroupMsg(groupId, it.reply, false)
                it.next?.let { it1 ->
                    deepMessage1 = it1
                } ?: break
            }
        }
        val selfSendMsg: SelfSendMsg = if (sendGroupMsg != null) {
            SelfSendMsg(sendGroupMsg.data.messageId.toLong(), message1)
        } else {
            SelfSendMsg(message1)
        }


        selfRecentlySendMessage.addMessageToQueue(groupId, selfSendMsg)
        return true
    }
}

package cn.luorenmu.common.extensions

import cn.luorenmu.common.utils.replaceCqToFileStr
import cn.luorenmu.dto.RecentlyMessageQueue
import cn.luorenmu.entiy.SelfSendMsg
import cn.luorenmu.repository.entiy.DeepMessage
import cn.luorenmu.repository.entiy.KeywordReply
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.action.common.ActionData
import com.mikuac.shiro.dto.action.common.MsgId
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.28 18:03
 */

private val selfRecentlySendMessage: RecentlyMessageQueue<SelfSendMsg> = RecentlyMessageQueue(40)
private val selfKeywordMessage: RecentlyMessageQueue<KeywordReply> = RecentlyMessageQueue(30)


/**
 *  true 表示消息队列已存在该消息
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

fun Bot.sendGroupMsgLimit(groupId: Long, message: String) {
    sendMsgLimit(groupId, message) {
        this.sendGroupMsg(groupId, message, false)
    }
}

fun Bot.sendGroupMsgLimit(groupId: Long, message: String, msgLimit: String) {
    sendMsgLimit(groupId, message, msgLimit) {
        this.sendGroupMsg(groupId, message, false)
    }
}

fun Bot.sendPrivateMsgLimit(id: Long, message: String) {
    sendMsgLimit(id, message) {
        this.sendPrivateMsg(id, message, false)
    }
}

fun Bot.sendGroupMsgKeywordLimit(id: Long, keywordReply: KeywordReply) {
    synchronized(selfKeywordMessage) {
        var send = false
        selfKeywordMessage.map[id]?.let {
            for (kw in it) {
                if ((keywordReply == kw || keywordReply.keyword == kw.keyword || keywordReply.reply == kw.reply)) {
                    return
                }
            }
        } ?: run {
            send = true
        }
        if (!selfRecentlySent(id, replaceCqToFileStr(keywordReply.reply) ?: keywordReply.reply)) {
            send = true
        }


        if (send) {
            selfKeywordMessage.addMessageToQueue(id, keywordReply)
            val sendList: ArrayList<String> = arrayListOf()
            sendList.add(keywordReply.reply)
            keywordReply.deepMessage(sendList, keywordReply.nextMessage)
            for (s in sendList) {
                sendGroupMsgLimit(id, keywordReply.reply, replaceCqToFileStr(keywordReply.reply) ?: "none")
                TimeUnit.SECONDS.sleep(1)
            }
        }
    }

}

private fun Bot.sendMsgLimit(id: Long, message: String, msgLimit: String = "none", send: () -> ActionData<MsgId>?) {
    synchronized(selfRecentlySendMessage) {
        var msgLimit1 = message
        if (msgLimit != "none") {
            msgLimit1 = msgLimit
        }
        if (selfRecentlySent(id, msgLimit1)) {
            return
        }
        val sendMsg = send()
        val selfSendMsg: SelfSendMsg = if (sendMsg != null && sendMsg.data != null) {
            SelfSendMsg(sendMsg.data.messageId.toLong(), msgLimit1)
        } else {
            SelfSendMsg(msgLimit1)
        }
        selfRecentlySendMessage.addMessageToQueue(id, selfSendMsg)
    }
}


fun Bot.sendGroupDeepMsgLimit(groupId: Long, message: String, deepMessage: DeepMessage?) {
    synchronized(selfRecentlySendMessage) {
        val selfSendMsgs = selfRecentlySendMessage.map[groupId]
        val message1 = message + deepMessage?.reply
        var deepMessage1 = deepMessage
        selfSendMsgs?.forEach {
            if (it.message == message1) {
                return
            }
        }
        val sendGroupMsg = this.sendGroupMsg(groupId, message, false)
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
    }
}

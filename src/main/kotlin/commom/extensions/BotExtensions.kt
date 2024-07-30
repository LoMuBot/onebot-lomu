package cn.luorenmu.commom.extensions

import cn.luorenmu.dto.RecentlyMessageQueue
import cn.luorenmu.dto.SelfSendMsg
import cn.luorenmu.repository.entiy.DeepMessage
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.action.common.ActionData
import com.mikuac.shiro.dto.action.common.MsgId
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.28 18:03
 */

private val selfRecentlySendMessage: RecentlyMessageQueue<SelfSendMsg> = RecentlyMessageQueue(8)

fun Bot.sendGroupMsgLimit(groupId: Long, message: String) {
    sendMsgLimit(groupId,message){
        this.sendGroupMsg(groupId,message,false)
    }
}

fun Bot.sendPrivateMsgLimit(id: Long, message: String) {
    sendMsgLimit(id,message){
        this.sendPrivateMsg(id,message,false)
    }
}

@Synchronized
private fun sendMsgLimit(id:Long, message: String, send: () -> ActionData<MsgId>?) {
    val selfSendMsgs = selfRecentlySendMessage.map[id]
    selfSendMsgs?.forEach {
        if (it.message == message) {
            return
        }
    }
    val sendMsg = send()
    val selfSendMsg: SelfSendMsg = if (sendMsg != null) {
        SelfSendMsg(sendMsg.data.messageId.toLong(), message)
    }else{
        SelfSendMsg(message)
    }
    selfRecentlySendMessage.addMessageToQueue(id, selfSendMsg)
}


@Synchronized
fun Bot.sendGroupDeepMsgLimit(groupId: Long, message: String, deepMessage: DeepMessage?) {
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
    }else{
        SelfSendMsg(message1)
    }


    selfRecentlySendMessage.addMessageToQueue(groupId, selfSendMsg)
}

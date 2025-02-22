package cn.luorenmu.config.shiro.customAction


import cn.luorenmu.config.shiro.customAction.response.RecordResponse
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.action.common.ActionData

/**
 * @author LoMu
 * Date 2024.09.10 04:51
 */



fun Bot.setMsgEmojiLike(msgId: String, face: String): ActionData<*> {
    val action = SetEmojiLikeActionPath.SetMsgEmojiLike
    // 构建请求参数
    val map = hashMapOf<String, Any>("emoji_id" to face, "message_id" to msgId)

    // 参考 Bot 类对响应结果进行处理
    return this.customRequest(action, map)

}

fun Bot.getGroupMsgHistory(groupId: Long, messageSeq: Int, count: Int): ActionData<*> {
    val action = GetGroupMsgHistoryActionPath.GetGroupMsgHistory
    // 构建请求参数
    val map = hashMapOf<String, Any>("group_id" to groupId, "message_seq" to messageSeq, "count" to count)
    // 参考 Bot 类对响应结果进行处理
    return this.customRequest(action, map)
}


fun Bot.getMsg(messageId: Long): ActionData<*> {
    val action = GetMessageActionPath.GetMsg
    // 构建请求参数
    val map = hashMapOf<String, Any>("message_id" to messageId)
    // 参考 Bot 类对响应结果进行处理
    return this.customRequest(action, map)
}



fun Bot.getRecord(file: String, outFormat: String): ActionData<RecordResponse> {
    val action = GetRecordActionPath.GetRecord
    // 构建请求参数
    val map = hashMapOf<String, Any>("file" to file, "out_format" to outFormat)

    // 参考 Bot 类对响应结果进行处理
    return this.customRequest(action, map, RecordResponse::class.java)

}
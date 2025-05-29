package cn.luorenmu.config.shiro.customAction


import cn.luorenmu.config.shiro.customAction.response.GetImageResponse
import cn.luorenmu.config.shiro.customAction.response.RecordResponse
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.action.common.ActionData
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * @author LoMu
 * Date 2024.09.10 04:51
 */

private val log = KotlinLogging.logger {}
/**
 * https://bot.q.qq.com/wiki/develop/api-v2/openapi/emoji/model.html#EmojiType
 */
fun Bot.setMsgEmojiLike(msgId: String, face: String): ActionData<*>? {
    try {
        val action = SetEmojiLikeActionPath.SetMsgEmojiLike
        val map = hashMapOf<String, Any>("emoji_id" to face, "message_id" to msgId)
        return this.customRequest(action, map)
    }catch (ignore: Exception){
        log.error { "${msgId}贴表情失败" }
    }
    return null
}

/**
 * @param messageSeq 指定消息id
 */
fun Bot.getGroupMsgHistory(groupId: Long, messageSeq: Int = 0, count: Int): ActionData<*> {
    val action = GetGroupMsgHistoryActionPath.GetGroupMsgHistory
    val map = hashMapOf<String, Any>("group_id" to groupId, "message_seq" to messageSeq, "count" to count)
    return this.customRequest(action, map)
}


fun Bot.getImage(file: String): ActionData<GetImageResponse> {
    val action = GetImageActionPath.GetImage
    val map = hashMapOf<String, Any>("file" to file)
    return this.customRequest(action, map, GetImageResponse::class.java)
}


fun Bot.getRecord(file: String, outFormat: String): ActionData<RecordResponse> {
    val action = GetRecordActionPath.GetRecord
    val map = hashMapOf<String, Any>("file" to file, "out_format" to outFormat)
    return this.customRequest(action, map, RecordResponse::class.java)

}
package cn.luorenmu.action

import cn.luorenmu.action.entiy.KeywordReplyJson
import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.entiy.DeepMessage
import cn.luorenmu.repository.entiy.KeywordReply
import com.alibaba.fastjson2.JSONObject
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.27 1:21
 */
@Component
class OneBotKeywordReply(private var keywordReplyRepository: KeywordReplyRepository) {
    fun jsonKeyword(id: Long, message: String): String? {
        JsonObjectUtils.getJsonObject("keyword")?.let { kf ->
            for (value in kf.values) {
                value as JSONObject
                value.let { json ->
                    json.getString("sender_id")?.let { senderId ->
                        if (senderId.toLong() == id) {
                            json.getList("keywords", KeywordReplyJson::class.java)?.let { keywords ->
                                for (keyword in keywords) {
                                    if (message.contains(keyword.message)) {
                                        return keyword.reply
                                    }
                                }
                            }
                        }
                    } ?: json.getList("keywords", KeywordReplyJson::class.java)?.let { keywords ->
                        for (keyword in keywords) {
                            if (message.contains(keyword.message)) {
                                return keyword.reply
                            }
                        }
                    }


                }
            }
        }
        return null
    }

    fun process(botId: Long, id: Long, message: String): KeywordReply? {
        return mongodbKeyword(botId, id, message)
    }


    private fun equals(msg: String, keywordReply: KeywordReply): Boolean {
        var equals = false
        if (keywordReply.needProcess) {
            if (msg.contains(Regex(keywordReply.keyword))) {
                equals = true
            }
        } else if (msg == keywordReply.keyword) {
            equals = true
        }
        return equals
    }


    fun triggerKeywords(
        botId: Long,
        msg: String,
        id: Long,
        atMe: Boolean,
        list: ArrayList<KeywordReply>,
        condition: () -> Boolean,
    ): Boolean {
        var found = false
        if (condition()) {
            val messages = keywordReplyRepository.findBySenderIdAndAtMe(id, atMe)
            var replaceMsg = msg
            val atMeStr = MsgUtils.builder().at(botId).build()
            if (msg.contains(atMeStr)) {
                replaceMsg = msg.replace(atMeStr, "").replace(" ", "")
            }
            if (messages.isNotEmpty()) {
                for (message in messages) {
                    if (equals(replaceMsg, message)) {
                        list.add(message)
                        found = true
                    }
                }
            }
        }
        return found
    }

    fun mongodbKeyword(botId: Long, id: Long, message: String): KeywordReply? {
        val random = ArrayList<KeywordReply>()

        // 没有at机器人指定senderId 为 id
        var exist = triggerKeywords(botId, message, id, false, random) { true }

        // 没有找到关键词向下搜索 at机器人并且指定发送人
        exist = triggerKeywords(botId, message, id, true, random) {
            !exist && message.contains(
                MsgUtils.builder().at(botId).build()
            )
        }

        // at机器人不指定发送人
        exist = triggerKeywords(botId, message, -1, true, random) {
            !exist && message.contains(
                MsgUtils.builder().at(botId).build()
            )
        }

        // 当指定发送者都没有成功获得相关消息时查询包含该关键词消息
        triggerKeywords(botId, message, -1, false, random) { !exist }

        if (random.isNotEmpty()) {
            return random.random()
        }
        return null
    }

}
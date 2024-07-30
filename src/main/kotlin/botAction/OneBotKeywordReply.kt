package cn.luorenmu.service

import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.entiy.DeepMessage
import cn.luorenmu.repository.entiy.KeywordReply
import cn.luorenmu.service.entiy.KeywordReplyJson
import com.alibaba.fastjson2.JSONObject
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Service

/**
 * @author LoMu
 * Date 2024.07.27 1:21
 */
@Service
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

    fun process(botId: Long, id: Long, message: String): List<String> {
        val replyList = ArrayList<String>()


        val mongodbKeyword = mongodbKeyword(botId, id, message)
        mongodbKeyword?.let {
            if (it.needProcess) {
                replyList.add(replyMessageReplace(it.reply))
            } else {
                replyList.add(it.reply)
            }
            it.nextMessage?.let { nextMessage ->
                replyList.add(nextMessage.reply)
                deepMessage(replyList, nextMessage)
            }
        }

        return replyList
    }

    fun replyMessageReplace(string: String): String {
        val scanDollarName = MatcherData.scanDollarName(string)
        if (scanDollarName.isPresent) {
            val name = scanDollarName.get()
            JsonObjectUtils.getString(name)?.let { js ->
                return MatcherData.replaceDollardName(string, name, js)
            }
        }
        return string
    }


    fun mongodbKeyword(botId: Long, id: Long, message: String): KeywordReply? {

        val senderMessages = keywordReplyRepository.findBySenderIdAndAtMe(id, false)
        val random = ArrayList<KeywordReply>()
        var specifySender = true
        if (senderMessages.isNotEmpty()) {
            for (senderMessage in senderMessages) {
                if (message.contains(Regex(senderMessage.keyword))) {
                    specifySender = false
                    random.add(senderMessage)
                }
            }
        }
        if (specifySender && message.contains(MsgUtils.builder().at(botId).build())) {
            val messages = keywordReplyRepository.findBySenderIdAndAtMe(-1, true)
            if (messages.isNotEmpty()) {
                for (senderMessage in messages) {
                    if (message.contains(Regex(senderMessage.keyword))) {
                        specifySender = false
                        random.add(senderMessage)
                    }
                }
            }
        }

        if (specifySender) {
            val messages = keywordReplyRepository.findBySenderIdAndAtMe(-1, false)
            if (messages.isNotEmpty()) {
                for (message1 in messages) {
                    if (message.contains(Regex(message1.keyword))) {
                        random.add(message1)
                    }
                }
            }
        }

        if (random.isNotEmpty()) {
            return random.random()
        }
        return null
    }

    private fun deepMessage(list: ArrayList<String>, deep: DeepMessage?): ArrayList<String> {
        deep?.next?.let {
            var reply = it.reply
            if (it.needProcess) {
                reply = replyMessageReplace(reply)
            }
            list.add(reply)
            deepMessage(list, deep)
        }
        return list
    }


}
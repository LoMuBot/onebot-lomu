package cn.luorenmu.action

import cn.luorenmu.common.extensions.isAt
import cn.luorenmu.common.extensions.sendGroupMsgKeywordLimit
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.entiy.KeywordReply
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import kotlin.random.Random

/**
 * @author LoMu
 * Date 2024.07.27 1:21
 */
@Component
class OneBotKeywordReply(
    private val stringRedisTemplate: StringRedisTemplate,
    private var keywordReplyRepository: KeywordReplyRepository,
) {

    @Async("keywordProcessThreadPool")
    fun process(bot: Bot, messageSender: MessageSender) {
        val atMe = messageSender.message.isAt(bot.selfId)
        if (atMe) {
            if (Random(System.currentTimeMillis()).nextInt(
                    0,
                    10
                ) <= 5
            ) {
                bot.setMsgEmojiLike(messageSender.messageId.toString(), "66")
            }
        }

        // 概率回复
        if (Random(System.currentTimeMillis()).nextDouble(
                0.0,
                1.0
            ) <= (stringRedisTemplate.opsForValue()["probability"]?.toDouble() ?: 0.1) || atMe
        ) {
            val mongodbKeyword = mongodbKeyword(bot.selfId, messageSender.senderId, messageSender.message)
            mongodbKeyword?.let {
                if (bot.sendGroupMsgKeywordLimit(messageSender.groupOrSenderId, it)) {
                    it.triggers?.run {
                        it.triggers = it.triggers!! + 1
                    } ?: run {
                        it.triggers = 1
                    }
                    keywordReplyRepository.save(it)
                }
            }
        }
    }


    /**
     * keyword match
     */
    private fun equals(msg: String, keywordReply: KeywordReply): Boolean {
        if (keywordReply.needProcess) {
            if (msg.contains(Regex(keywordReply.keyword))) {
                return true
            }
        } else if (msg == keywordReply.keyword) {
            return true
        }
        return false
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

        // 指定用户
        if (condition()) {
            val msgLists = keywordReplyRepository.findBySenderIdAndAtMeAndNeedProcess(id, atMe, true)
            msgLists.addAll(keywordReplyRepository.findBySenderIdAndAtMeAndKeyword(id, atMe, msg))
            var replaceMsg = msg
            val atMeStr = MsgUtils.builder().at(botId).build()
            if (msg.contains(atMeStr)) {
                replaceMsg = msg.replace(atMeStr, "").replace(" ", "")
            }
            if (msgLists.isNotEmpty()) {
                for (message in msgLists) {
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
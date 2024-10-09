package cn.luorenmu.action

import cn.luorenmu.common.extensions.addMsgLimit
import cn.luorenmu.common.extensions.checkThenSave
import cn.luorenmu.common.extensions.getCQFileStr
import cn.luorenmu.common.extensions.isCQAt
import cn.luorenmu.common.extensions.isCQJson
import cn.luorenmu.common.extensions.isCQReply
import cn.luorenmu.common.extensions.isCQStr
import cn.luorenmu.common.extensions.isImage
import cn.luorenmu.common.extensions.isMface
import cn.luorenmu.common.extensions.replaceCqToFileStr
import cn.luorenmu.common.extensions.selfRecentlySent
import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.extensions.toPinYin
import cn.luorenmu.listen.groupMessageQueue
import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.entiy.ActiveMessage
import cn.luorenmu.repository.entiy.KeywordReply
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.ansj.splitWord.analysis.ToAnalysis
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * @author LoMu
 * Date 2024.07.31 0:15
 */
@Component
class OneBotChatStudy(
    private val keywordReplyRepository: KeywordReplyRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val activeSendMessageRepository: ActiveSendMessageRepository,
) {
    // 复读数 size + 1
    private val size = 3

    fun process(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val groupId = groupMessageEvent.groupId
        val originMessage = groupMessageEvent.message
        if (reRead(bot, groupMessageEvent)) {
            synchronized(groupMessageQueue.map[groupMessageEvent.groupId]!!) {
                reReadStudy(groupId, originMessage)
            }
        } else {
            autoStudy(bot, groupMessageEvent)
        }
    }


    @Async
    fun autoStudy(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val groupId = groupMessageEvent.groupId
        val originMessage = groupMessageEvent.message
        val message = originMessage.replace("@\\S+".toRegex(), "")
        if (message.isBlank() || message.isCQAt() || message.isCQReply()) {
            return
        }
        if (message.isMface()) {
            activeSendMessageRepository.checkThenSave(ActiveMessage(null, -1, message, null))
        }

        val lastMessages = groupMessageQueue.lastMessages(groupId, 6)
        val currentMessagePinYin = message.toPinYin()

        val scoreMap = hashMapOf<String, Int>()

        if (lastMessages.isNotEmpty()) {
            for (groupMessage in lastMessages) {
                val keyword = groupMessage!!.groupEventObject.message

                // 不处理
                if (keyword.isBlank() || keyword.isImage() || keyword.isCQReply() || keyword.length < 2
                    || originMessage.replace(" ", "") == keyword.replace(" ", "")
                    || keyword.contains("查询") || (keyword.isCQStr() && message.isCQStr()) || keyword.isCQJson()
                ) {
                    continue
                }

                // 分词筛选
                val results =
                    ToAnalysis.parse(keyword).terms.stream().map { ta -> ta.realName }.distinct().toList()
                var score = 0
                for (result in results) {
                    if (currentMessagePinYin.contains(result.toPinYin())) {
                        score++
                    }

                }
                if (score != 0) {
                    scoreMap.put(keyword, score)
                }
            }
            // 打分 将最高分作为keyword
            if (scoreMap.isNotEmpty()) {
                val maxScore = scoreMap.maxBy { it.value }
                if (maxScore.value < 3) {
                    return
                }
                val maxScoreStr = maxScore.key
                bot.addMsgLimit(groupId, maxScoreStr)

                keywordReplyRepository.checkThenSave(
                    KeywordReply(
                        null,
                        -1L,
                        maxScoreStr,
                        message,
                        false,
                        false,
                        groupId,
                        LocalDateTime.now(),
                        0,
                        null
                    )
                )
            }

        }


    }

    fun reReadStudy(groupId: Long, message: String) {
        //复读 回复或者At了某人 反正就是禁止这样的添加到关键词
        if (message.isCQAt() || message.isCQReply()) {
            return
        }


        //机器人已经复读了 将存储复读的头信息作为keyword
        val lastMessageList = groupMessageQueue.lastMessages(groupId, size + 5)
        if (lastMessageList.isNotEmpty()) {
            var keyword: String? = null
            val currentMessage = message.replaceCqToFileStr() ?: message
            val currentMessagePinYin = currentMessage.toPinYin()

            // 遍历查找
            for (lastGroupMessage in lastMessageList) {
                // 如果为图片获取图片名
                val lastMessage = lastGroupMessage!!.groupEventObject.message.replaceCqToFileStr()
                    ?: lastGroupMessage.groupEventObject.message
                if (lastMessage != currentMessage) {
                    // 限制保存
                    if (lastMessage.isCQReply()) {
                        continue
                    }

                    val results =
                        ToAnalysis.parse(lastMessage).terms.stream().filter { ta -> ta.realName.length >= 2 }
                            .map { ta -> ta.realName }.toList()
                    for (result in results) {
                        if (currentMessagePinYin.contains(result.toPinYin())) {
                            keyword = lastMessage
                            break
                        }
                    }
                }
            }


            // 无法找到 再次尝试查找图片
            keyword ?: run {
                val lastMessageList = groupMessageQueue.lastMessages(groupId, size + 3)
                if (lastMessageList.isNotEmpty()) {
                    val currentMessage = message.replaceCqToFileStr() ?: message

                    // 遍历查找
                    for (lastGroupMessage in lastMessageList) {
                        val lastMessage = lastGroupMessage!!.groupEventObject.message.replaceCqToFileStr()
                            ?: lastGroupMessage.groupEventObject.message
                        if (lastMessage != currentMessage) {
                            if (lastMessage.isImage() || !lastMessage.isCQReply() || lastMessage.isMface()) {
                                keyword = lastMessage
                                break
                            }
                        }
                    }
                }
            }

            // 无法找到 将其添加至主动消息
            keyword ?: run {
                activeSendMessageRepository.checkThenSave(ActiveMessage(null, -1, message, null))
                return
            }

            var needProcess = false
            keyword = if (keyword.isImage()) {
                needProcess = true
                keyword.getCQFileStr()!!
            } else {
                keyword
            }


            val message1 = message.replace("@\\S+".toRegex(), "")
            if (message1.isBlank()) {
                return
            }

            val k = KeywordReply(
                null, -1L,
                keyword,
                message1,
                needProcess,
                atMe = false,
                groupId = groupId,
                createdDate = LocalDateTime.now(),
                0,
                nextMessage = null
            )
            keywordReplyRepository.checkThenSave(k)
        }


    }

    fun reRead(bot: Bot, groupMessageEvent: GroupMessageEvent): Boolean {
        val senderId = groupMessageEvent.sender.userId
        val groupId = groupMessageEvent.groupId
        var message = groupMessageEvent.message


        val lastMessages = groupMessageQueue.lastMessages(groupId, size)


        var isReRead = false
        if (lastMessages.isNotEmpty()) {
            var repeatNum = 0

            lastMessages.forEach {
                val userId = it?.groupEventObject?.sender?.userId
                var lastMsg = it!!.groupEventObject.message
                var currentMsg = message
                if (lastMsg.isImage() && currentMsg.isImage()) {
                    currentMsg = currentMsg.getCQFileStr() ?: currentMsg
                    lastMsg = lastMsg.getCQFileStr() ?: lastMsg
                }

                if (userId != senderId && currentMsg == lastMsg) {
                    repeatNum++
                }
            }
            if (repeatNum == lastMessages.size) {
                if (!selfRecentlySent(groupId, message.replaceCqToFileStr() ?: message)) {
                    isReRead = true
                } else {
                    return false
                }
                val msgLimit = message.replaceCqToFileStr() ?: "none"

                //复读
                redisTemplate.opsForValue()["isReRead"]?.let {
                    if (Random(System.currentTimeMillis()).nextInt(0, 10) <= 3) {
                        bot.sendGroupMsgLimit(groupId, message, msgLimit)
                    } else {
                        bot.addMsgLimit(groupId, message, msgLimit)
                    }
                } ?: run {
                    bot.addMsgLimit(groupId, message, msgLimit)
                }

            }
        }

        return isReRead
    }
}
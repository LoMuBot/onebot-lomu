package cn.luorenmu.action.chat

import cn.luorenmu.action.commandProcess.botCommand.ChatStudyCommand
import cn.luorenmu.common.extensions.*
import cn.luorenmu.listen.GroupEventListen.Companion.groupMessageQueue
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
open class OneBotChatStudy(
    private val keywordReplyRepository: KeywordReplyRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val activeSendMessageRepository: ActiveSendMessageRepository,
    private val chatStudyCommand: ChatStudyCommand,
) {
    // 复读数 size + 1
    private val size = 3

    @Async("asyncProcessThreadPool")
    open fun process(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        if (!chatStudyCommand.state(groupMessageEvent.groupId)) {
            return
        }
        val groupId = groupMessageEvent.groupId
        val originMessage = groupMessageEvent.message
        if (reRead(bot, groupMessageEvent)) {
            synchronized(groupMessageQueue.map[groupMessageEvent.groupId]!!) {
                reReadStudy(groupId, originMessage)
            }
        }
        autoStudy(bot, groupMessageEvent)
    }


    /**
     * 保存表情
     */
    @Async
    open fun autoStudy(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val originMessage = groupMessageEvent.message
        val message = originMessage.replace("@\\S+".toRegex(), "")
        if (message.isBlank() || message.isCQAt() || message.isCQReply()) {
            return
        }
        if (message.isMface()) {
            activeSendMessageRepository.checkThenSave(ActiveMessage(null, -1, message, null))
        }
    }

    private fun reReadStudy(groupId: Long, message: String) {
        //复读 回复或者At了某人 反正就是禁止这样的添加到关键词
        if (message.isCQAt() || message.isCQReply()) {
            return
        }


        //机器人已经复读了 将存储复读的头信息作为keyword
        val lastMessageListSize5 = groupMessageQueue.lastMessages(groupId, size + 5)
        if (lastMessageListSize5.isNotEmpty()) {
            var keyword: String? = null
            val currentMessagePinYin = message.toPinYin()

            // 遍历查找
            for (lastGroupMessage in lastMessageListSize5) {
                // 如果为图片获取图片名
                val lastMessage = lastGroupMessage.groupEventObject.message
                if (lastMessage != message) {
                    // 限制保存
                    if (lastMessage.isCQReply()) {
                        continue
                    }

                    val results =
                        ToAnalysis.parse(lastMessage).terms.stream()
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
                if (lastMessageListSize5.isNotEmpty()) {

                    // 遍历查找
                    for (lastGroupMessage in lastMessageListSize5) {
                        val lastMessage = lastGroupMessage.groupEventObject.message.replaceImgCqToFileStr()
                        if (lastMessage != message) {
                            if (lastMessage.isImage() || !lastMessage.isCQReply() || lastMessage.isMface()) {
                                keyword = lastMessage
                                break
                            }
                        }
                    }
                }
            }

            // 无法找到
            keyword ?: run {
                return
            }

            keyword = if (keyword!!.isImage()) {
                keyword!!.getCQFileStr()!!
            } else {
                keyword
            }


            val message1 = message.replace("@\\S+".toRegex(), "")
            if (message1.isBlank()) {
                return
            }

            val k = KeywordReply(
                null, -1L,
                keyword!!,
                message1,
                false,
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
        val message = groupMessageEvent.message


        val lastMessages = groupMessageQueue.lastMessages(groupId, size)


        var isReRead = false
        if (lastMessages.isNotEmpty()) {
            var repeatNum = 0

            lastMessages.forEach {
                val userId = it.groupEventObject.sender.userId
                var lastMsg = it.groupEventObject.message
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
                if (!selfRecentlySent(groupId, message)) {
                    isReRead = true
                } else {
                    return false
                }

                //复读
                redisTemplate.opsForValue()["isReRead"]?.let {
                    if (Random(System.currentTimeMillis()).nextInt(0, 10) <= 3) {
                        bot.sendGroupMsgLimit(groupId, message)
                    } else {
                        bot.addMsgLimit(groupId, message)
                    }
                } ?: run {
                    bot.addMsgLimit(groupId, message)
                }

            }
        }

        return isReRead
    }
}
package cn.luorenmu.action

import cn.luorenmu.common.extensions.addMsgLimit
import cn.luorenmu.common.extensions.selfRecentlySent
import cn.luorenmu.common.utils.*
import cn.luorenmu.listen.groupMessageQueue
import cn.luorenmu.repository.KeywordReplyRepository
import cn.luorenmu.repository.entiy.KeywordReply
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.31 0:15
 */
@Component
class OneBotCommonHandle(
    private var keywordReplyRepository: KeywordReplyRepository,
) {


    fun reRead(bot: Bot, groupMessageEvent: GroupMessageEvent) {
        val senderId = groupMessageEvent.sender.userId
        val groupId = groupMessageEvent.groupId
        val message = groupMessageEvent.message

        // 复读数
        val size = 2
        val lastMessages = groupMessageQueue.lastMessages(groupId, size)


        var isReRead = false
        if (lastMessages.isNotEmpty()) {
            var repeatNum = 0

            lastMessages.forEach {
                val userId = it?.groupEventObject?.sender?.userId
                var lastMsg = it!!.groupEventObject.message
                var currentMsg = message
                if (isImage(lastMsg) && isImage(currentMsg)) {
                    currentMsg = getCQFileStr(currentMsg) ?: currentMsg
                    lastMsg = getCQFileStr(lastMsg) ?: lastMsg
                }

                if (userId != senderId && currentMsg == lastMsg) {
                    repeatNum++
                }
            }
            if (repeatNum == lastMessages.size) {
                if (!selfRecentlySent(groupId, replaceCqToFileStr(message) ?: message)) {
                    isReRead = true
                } else {
                    return
                }
                val msgLimit = replaceCqToFileStr(message) ?: "none"

                //不再复读
                bot.addMsgLimit(groupId, message, msgLimit)
            }
        }

        //机器人已经复读了 将存储复读的头信息作为keyword
        if (isReRead) {
            val lastMessageList = groupMessageQueue.lastMessages(groupId, size + 1)
            if (lastMessageList.isNotEmpty()) {
                var keyword: String? = null
                val currentMessage = replaceCqToFileStr(message) ?: message
                for (lastGroupMessage in lastMessageList) {
                    val lastMessage = replaceCqToFileStr(lastGroupMessage!!.groupEventObject.message)
                        ?: lastGroupMessage.groupEventObject.message
                    if (lastMessage != currentMessage) {
                        keyword = lastMessage
                        break
                    }

                }
                keyword ?: run {
                    return
                }

                var needProcess = false
                keyword = if (isImage(keyword)) {
                    needProcess = true
                    getCQFileStr(keyword)!!.replace(Regex("[{}]"), "")
                } else {
                    keyword
                }

                //复读 回复或者At了某人 反正就是禁止这样的添加到关键词
                if (isCQAt(keyword) || isCQReply(keyword)) {
                    return
                }

                val k = KeywordReply(
                    null, -1L,
                    keyword,
                    message,
                    needProcess,
                    atMe = false,
                    nextMessage = null
                )
                synchronized(keywordReplyRepository) {
                    keywordReplyRepository.findByKeywordIsAndReplyIs(message, message) ?: run {
                        keywordReplyRepository.insert(k)
                    }
                }
            }

        }
    }
}
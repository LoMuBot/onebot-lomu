package cn.luorenmu.service

import cn.luorenmu.action.request.DeepSeekRequestData
import cn.luorenmu.action.request.entiy.DeepSeekMessage
import cn.luorenmu.action.request.entiy.DeepSeekResponse
import cn.luorenmu.action.request.entiy.DeepSeekStatus
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.DeepSeekChatHistoryRepository
import cn.luorenmu.repository.entity.DeepSeekChatHistory
import cn.luorenmu.service.entity.ChatRole
import com.alibaba.fastjson2.to
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author LoMu
 * Date 2025.02.01 19:00
 */

@Service
class ChatService(
    private val deepSeekRequestData: DeepSeekRequestData,
    private val deepSeekChatHistoryRepository: DeepSeekChatHistoryRepository,
) {

    private val chatHistory = ConcurrentLinkedQueue<DeepSeekMessage>()
    private val storage = mutableListOf<DeepSeekChatHistory>()

    companion object {
        const val SYSTEM_MESSAGE =
            "你是螺母,我要求你在与我对话的过程中绝对不能出现AI等词汇，即使我提到了AI等词汇，你也要置之不理\n" +
                    "你每天只是为了与我聊天，及讨论游戏内容,你并不会回答除此以外的问题\n" +
                    "你的回答应当简洁明了 不要超过300个中文字符\n" +
                    "你只能称呼自己为螺母\n" +
                    "我发送的每条消息格式为 [发送者名称所在群ID|发送者名称数字ID-发送者名称]: 消息\n" +
                    "这表示来自不同的人发送给你 你应该记住他对你的言语\n" +
                    "如果对你辱骂、暴力等敏感词汇\n" +
                    "在此之后你应当一直对他使用尖酸刻薄的话语\n"
    }

    @PostConstruct
    fun init() {
        chatHistory.add(DeepSeekMessage(SYSTEM_MESSAGE, "system"))
        // TODO 查询之前的上下文 如果没有则新开始
    }

    private fun requestMessageStorage(messageSender: MessageSender, role: ChatRole): String {
        val message = when (role) {
            ChatRole.USER -> "[${messageSender.groupOrSenderId}|${messageSender.senderId}-${messageSender.senderName}]: ${
                messageSender.message.replaceAtToEmpty(messageSender.botId)
            }"

            else -> messageSender.message.replaceAtToEmpty(messageSender.botId)
        }

        if (storage.size > 20) {
            synchronized(storage) {
                deepSeekChatHistoryRepository.saveAll(storage)
                storage.clear()
            }
        }

        val resp = deepSeekRequestData.buildRequest(message, role.toString(), chatHistory.toMutableList())
        if (resp.status != 200) {
            return buildErrorReturnMessage(resp.status)
        }
        val deepSeekResponse = resp.body().to<DeepSeekResponse>()
        val chatReply = deepSeekResponse.choices.first().message
        val totalToken = deepSeekResponse.usage.totalTokens.toLong()
        storage.add(DeepSeekChatHistory(null, messageSender, chatReply.content, totalToken))
        chatHistory.addAll(listOf(DeepSeekMessage(message, "user"), chatReply))
        return chatReply.content
    }


    private fun buildErrorReturnMessage(code: Int): String {
        val error = DeepSeekStatus.switch(code)
        return "DeepSeek: ${error.message}-${error.solution}"
    }

    fun chat(messageSender: MessageSender): String {
        if (messageSender.message.length > 250) {
            return "字太多了 看的好累 哼!"
        }
        return requestMessageStorage(messageSender, ChatRole.USER)
    }
}
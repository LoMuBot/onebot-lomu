package cn.luorenmu.repository.entity

import cn.luorenmu.action.request.entiy.DeepSeekResponse
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.02.02 01:54
 */
@Document(collection = "deep_seek_chat_history")
data class DeepSeekChatHistory(
    @Id
    var id: String? = null,
    @Indexed
    val messageSender: MessageSender,
    val deepSeekReply: String,
    val totalTokens: Long,
    val deepSeekResponse: DeepSeekResponse,
    val date: LocalDateTime = LocalDateTime.now(),
)

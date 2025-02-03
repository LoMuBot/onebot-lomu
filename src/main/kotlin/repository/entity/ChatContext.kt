package cn.luorenmu.repository.entity

import cn.luorenmu.action.request.entiy.DeepSeekMessage
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.02.02 23:45
 */

@Document(collection = "chat_context")
data class ChatContext(
    @Id
    var id: String?,
    @Indexed
    var groupId: Long,
    val sendMessage: DeepSeekMessage,
    val replyMessage: DeepSeekMessage,
    val date: LocalDateTime,
)

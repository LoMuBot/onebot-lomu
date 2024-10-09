package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.19 21:59
 */

@Document(collection = "record_message")
data class RecordMessage(
    @Id
    val id: String?,
    val senderId: Long,
    val groupId: Long,
    val path: String,
    val createdAt: LocalDateTime
)

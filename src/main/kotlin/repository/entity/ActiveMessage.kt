package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author LoMu
 * Date 2024.07.30 2:59
 */

@Document(collection = "active_message")
data class ActiveMessage(
    @Id
    var id: String?,
    @Indexed
    var groupId: Long,
    var message: String,
    var nextMessage: DeepMessage? = null,
)

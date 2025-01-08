package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed


/**
 * @author LoMu
 * Date 2024.08.11 23:53
 */
data class AtStudyMessage(
    @Id
    var id: String,
    @Indexed
    var groupId: Long,
    var message: String,
    val rawMessage: String,
    var atCount: Int
)
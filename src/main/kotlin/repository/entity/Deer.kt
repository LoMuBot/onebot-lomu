package cn.luorenmu.repository.entiy

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author LoMu
 * Date 2025.01.07 04:48
 */

@Document(collection = "deer")
data class Deer(
    val id: String?,
    @Indexed
    val senderId: Long,
    val year: Int,
    val month: Int,
    var count: Int?,
    val days: MutableList<Int>,
)

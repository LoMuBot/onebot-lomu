package cn.luorenmu.entiy

import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.13 12:35
 */
data class WaitDeleteFile(
    val path: String,
    val createDate: LocalDateTime = LocalDateTime.now(),
    val deleteDate: LocalDateTime
)

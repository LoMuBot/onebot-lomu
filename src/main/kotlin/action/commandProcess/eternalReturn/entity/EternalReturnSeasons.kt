package cn.luorenmu.action.commandProcess.eternalReturn.entiy

/**
 * @author LoMu
 * Date 2024.08.05 7:18
 */
data class EternalReturnSeasons(
    val id: Int,
    val key: String,
    val name: String,
    val isCurrent: Boolean = false
)

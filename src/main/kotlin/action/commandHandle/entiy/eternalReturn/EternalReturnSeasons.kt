package cn.luorenmu.action.commandHandle.entiy.eternalReturn

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

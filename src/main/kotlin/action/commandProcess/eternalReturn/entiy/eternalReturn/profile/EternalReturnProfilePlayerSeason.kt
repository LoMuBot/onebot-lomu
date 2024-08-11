package cn.luorenmu.action.commandHandle.entiy.eternalReturn.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:31
 */
data class EternalReturnProfilePlayerSeason(
    val seasonID: Long,
    val mmr: Long? = null,
    val tierID: Long? = null,
    val tierGradeID: Long? = null,
    val tierMmr: Long? = null,
)

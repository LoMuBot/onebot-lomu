package cn.luorenmu.action.commandProcess.eternalReturn.entiy

/**
 * @author LoMu
 * Date 2024.07.31 9:23
 */
data class EternalReturnPlayerTierByUserNum(
    val imageUrl: String,
    val lp: Int,
    val mmr: Int,
    // 段位名
    val name: String,
    val seasonId: Int,
    val tierGrade: Int,
    val tierType: Int,
)

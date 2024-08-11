package cn.luorenmu.action.commandHandle.entiy.eternalReturn

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

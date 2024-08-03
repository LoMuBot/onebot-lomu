package cn.luorenmu.action.commandHandle.entiy.eternalReturn

/**
 * @author LoMu
 * Date 2024.07.31 9:05
 */
data class EternalReturnLeaderboardPlayer(
    val avgPlacement: Double,
    val avgPlayerKill: Double,
    val characterIds: ArrayList<Int>?,
    val mmr: Int,
    val mostCharacters: ArrayList<EternalReturnCharacterPickRate>,
    val nickname: String,
    val playCount: Int,
    val rank: Int,
    val rankDiff: Int,
    val top3Rate: Double,
    val userNum: Long,
    val winRate: Double,
)

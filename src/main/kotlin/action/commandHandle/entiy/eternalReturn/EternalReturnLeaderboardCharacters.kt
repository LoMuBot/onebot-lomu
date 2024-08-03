package cn.luorenmu.action.commandHandle.entiy.eternalReturn

/**
 * @author LoMu
 * Date 2024.07.31 12:45
 */

/**
 *  排行榜
 *  url -> https://dak.gg/er/leaderboard
 */
data class EternalReturnLeaderboardCharacters(
    val characterById: HashMap<Int, EternalReturnCharacterById>,
    val createdAt: Long,
    val minMatchCount: Int,
    val playerTierByUserNum: HashMap<Int, EternalReturnPlayerTierByUserNum>,
    val rankings: ArrayList<EternalReturnRankings>,
    val totalCount: Int,
)

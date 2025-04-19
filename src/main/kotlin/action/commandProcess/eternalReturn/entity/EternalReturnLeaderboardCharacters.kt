package action.commandProcess.eternalReturn.entity

import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnPlayerTierByUserNum
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnRankings

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

package cn.luorenmu.action.commandProcess.eternalReturn.entiy

import cn.luorenmu.action.commandHandle.entiy.eternalReturn.EternalReturnCharacterById
import cn.luorenmu.action.commandHandle.entiy.eternalReturn.EternalReturnCutoffs
import cn.luorenmu.action.commandHandle.entiy.eternalReturn.EternalReturnLeaderboardPlayer
import cn.luorenmu.action.commandHandle.entiy.eternalReturn.EternalReturnPlayerTierByUserNum
import cn.luorenmu.action.commandHandle.entiy.eternalReturn.EternalReturnTierDistributionDtos

/**
 * @author LoMu
 * Date 2024.07.31 8:59
 */
data class EternalRetrunLeaderboard(
    val characterById: HashMap<Int, EternalReturnCharacterById>,
    val cutoffs: ArrayList<EternalReturnCutoffs>,
    val leaderboards: ArrayList<EternalReturnLeaderboardPlayer>,
    val playerTierByUserNum: HashMap<Int, EternalReturnPlayerTierByUserNum>,
    val tierDistributionDtos: ArrayList<EternalReturnTierDistributionDtos>,
    val totalLeaderBoardCount: Int,
    val updatedAt: Long,
    var currentSeason: EternalReturnCurrentSeason? = null,
)



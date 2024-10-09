package cn.luorenmu.action.commandProcess.eternalReturn.entiy

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



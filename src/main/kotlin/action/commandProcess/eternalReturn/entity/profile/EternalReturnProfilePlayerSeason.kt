package action.commandProcess.eternalReturn.entity.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:31
 */
data class EternalReturnProfilePlayerSeason(
    val seasonId: Int,
    var mmr: Int,
    var tierId: Int,
    var tierGradeId: Long,
    var tierMmr: Long
)

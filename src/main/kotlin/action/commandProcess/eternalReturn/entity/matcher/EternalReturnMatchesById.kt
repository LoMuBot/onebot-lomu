package cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher

/**
 * @author LoMu
 * Date 2025.06.24 13:48
 */
data class EternalReturnMatchesById(
    val matches: List<EternalReturnMatches.Match>,
    val playerTiers: List<EternalReturnPlayerTier>,
) {
    data class EternalReturnPlayerTier(
        val tierId: Int,
        val tierGradeId: Long,
        val tierMmr: Long,
        val mmr: Int,
        val userNum: Int,
    )
}

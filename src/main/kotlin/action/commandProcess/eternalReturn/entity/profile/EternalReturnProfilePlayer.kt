package action.commandProcess.eternalReturn.entity.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:10
 */
data class EternalReturnProfilePlayer(
    val accountLevel: Int,
    val lastPlayedSeasonId: Int,
    val name: String,
    val syncedAt: Long,
    val userNum: Int
)

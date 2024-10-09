package cn.luorenmu.action.commandProcess.eternalReturn.entiy.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:25
 */
data class EternalReturnProfileDuoStat(
    val userNum: Long,
    val nickname: String,
    val updatedAt: Long,
    val play: Long,
    val win: Long,
    val place: Long,
    val characterStats: List<EternalReturnProfileCharacterStat>
)

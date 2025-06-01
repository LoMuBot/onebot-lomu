package action.commandProcess.eternalReturn.entity.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:20
 */
data class EternalReturnProfileStat(
    val key: Long,
    val updatedAt: Long? = null,
    val play: Int,
    val win: Long,
    val top2: Long,
    val top3: Long,
    val place: Long,
    val playerKill: Long,
    val playerAssistant: Long,
    val teamKill: Long,
    val monsterKill: Long,
    val damageToPlayer: Int,
    val damageToMonster: Long,
    val mmrGain: Int,
    val playTime: Long,
    val playerDeaths: Long,
    val weaponStats: List<EternalReturnProfileStat>?,
    val skinStats: List<EternalReturnProfileStat>? = null,
)

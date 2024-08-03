package cn.luorenmu.action.commandHandle.entiy.eternalReturn.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:12
 */

/**
 * 赛季概括
 */
data class EternalReturnProfilePlayerSeasonOverviews(
    val userNum: Long,
    val seasonID: Long,
    val matchingModeID: Long,
    val teamModeID: Long,
    val updatedAt: Long,
    val mmr: Long,
    val play: Long,
    val win: Long,
    val top2: Long,
    val top3: Long,
    val place: Long,
    val playerKill: Long,
    val playerAssistant: Long,
    val teamKill: Long,
    val monsterKill: Long,
    val damageToPlayer: Long,
    val damageToMonster: Long,
    val mmrGain: Long,
    val playTime: Long,
    val playerDeaths: Long,
    val characterStats: List<EternalReturnProfileStat>,
    val serverStats: List<EternalReturnServerStat>,
    val mmrStats: List<List<Long>>,
    val duoStats: List<EternalReturnProfileDuoStat>,
    val recentMatches: List<Map<String, Long>>,
    val tierID: Long? = null,
    val tierGradeID: Long? = null,
    val tierMmr: Long? = null,
    val rank: EternalReturnProfileRank? = null
)

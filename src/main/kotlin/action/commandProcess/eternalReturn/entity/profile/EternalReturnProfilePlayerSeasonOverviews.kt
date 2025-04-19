package action.commandProcess.eternalReturn.entity.profile

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
    // 3为排位模式，2为匹配模式 6为钴协议 0为全部
    val matchingModeId: Int,
    val teamModeId: Int,
    val updatedAt: Long,
    val mmr: Int,
    val play: Int,
    val win: Int,
    val top2: Int,
    val top3: Int,
    val place: Int,
    val playerKill: Int,
    val playerAssistant: Int,
    val teamKill: Int,
    val monsterKill: Int,
    val damageToPlayer: Int,
    val damageToMonster: Int,
    val mmrGain: Int,
    val playTime: Long,
    val playerDeaths: Int,
    val characterStats: List<EternalReturnProfileStat>,
    val serverStats: List<EternalReturnServerStat>,
    val mmrStats: List<List<Long>>,
    val duoStats: List<EternalReturnProfileDuoStat>,
    val recentMatches: List<RecentGameMatcher>,
    var tierId: Long? = null,
    var tierGradeId: Long? = null,
    var tierMmr: Long? = null,
    var rank: EternalReturnProfileRank? = null
){
    data class RecentGameMatcher(
        val gameId: Long,
        val seasonId: Int,
        // 3为排位模式，2为匹配模式 6为钴协议 0为全部
        val matchingMode: Int,
        val teamMode: Int,
        val characterNum: Int,
        val skinCode: Int,
        val gameRank: Int,
        val playerKill: Int,
        val playerAssistant: Int,
        val monsterKill: Int,
        val bestWeapon: Int,
        val mmrGain: Int,
        val preMade: Int,
        val damageToPlayer: Int,
        val damageToMonster: Int,
        val giveUp: Int,
        val teamKill: Int,
        val playerDeaths: Int,
        val escapeState: Int
    )
}

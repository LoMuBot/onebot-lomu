package cn.luorenmu.action.commandHandle.entiy.eternalReturn

/**
 * @author LoMu
 * Date 2024.07.31 12:50
 */
data class EternalReturnRankings(
    val avgDamageToPlayer : Double,
    val avgDamageToMonster : Double,
    val avgMonsterKill: Double,
    val avgPlacement: Double,
    val avgPlayerAssistant: Double,
    val avgPlayerKill: Double,
    val characterId : Int,
    val maxMonsterKill : Int,
    val maxPlayerKill : Int,
    val mmr : Int,
    val nickname: String,
    val pickCount:String,
    val userNum: Long
)

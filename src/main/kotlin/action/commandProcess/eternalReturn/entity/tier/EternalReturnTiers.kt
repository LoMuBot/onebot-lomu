package cn.luorenmu.action.commandProcess.eternalReturn.entity.tier

/**
 * @author LoMu
 * Date 2025.03.29 21:22
 */
/**
 * 段位信息
 */
data class EternalReturnTiers(
    val tiers: ArrayList<EternalReturnTier>,
){
    data class EternalReturnTier(
        val id: Int,
        val key: String,
        val name: String,
        val imageUrl: String,
        val iconUrl:String
    )
}

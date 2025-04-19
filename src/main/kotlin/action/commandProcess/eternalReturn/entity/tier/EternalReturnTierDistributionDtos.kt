package action.commandProcess.eternalReturn.entity.tier

/**
 * @author LoMu
 * Date 2024.07.31 9:25
 */
// 段位图 >= 无暇
data class EternalReturnTierDistributionDtos(
    val count: Int,
    val rate: Double,
    val tierGrade: Int,
    val tierImageUrl: String,
    val tierType: Int,
)

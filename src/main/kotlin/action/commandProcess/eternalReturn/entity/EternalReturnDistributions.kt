package cn.luorenmu.action.commandProcess.eternalReturn.entiy

/**
 * @author LoMu
 * Date 2024.08.03 9:42
 */

// EternalReturnTierDistributions的子对象
data class EternalReturnDistributions(
    val count: Int,
    val rate: Double,
    val tierGrade: Int,
    val tierImageUrl: String,
    val tierType: Int,
) {
}
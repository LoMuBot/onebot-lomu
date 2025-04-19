package action.commandProcess.eternalReturn.entity.tier

import action.commandProcess.eternalReturn.entity.EternalReturnDistributions

/**
 * @author LoMu
 * Date 2024.08.03 9:41
 */

/**
 *  段位分布
 *  url -> https://dak.gg/er/statistics/tier?teamMode=SQUAD
 */
data class EternalReturnTierDistributions(
    val distributions: ArrayList<EternalReturnDistributions>,
    val updatedAt: Long,
)
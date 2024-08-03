package cn.luorenmu.action.commandHandle.entiy.eternalReturn.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:26
 */
/**
 *  高手 进入了 前1000名
 */
data class EternalReturnProfileRank(
    val in1000: EternalReturnProfileRankGlobal,
    val local: EternalReturnProfileRankGlobal,
    val global: EternalReturnProfileRankGlobal,
)
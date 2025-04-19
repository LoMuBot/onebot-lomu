package cn.luorenmu.action.commandProcess.eternalReturn.entity.weapon

/**
 * @author LoMu
 * Date 2025.04.15 18:03
 */
data class EternalReturnWeapons(
    val masteries: List<EternalReturnWeapon>,
) {
    data class EternalReturnWeapon(
        val id: Int,
        val key: String,
        val name: String,
        val iconUrl: String,
    )
}

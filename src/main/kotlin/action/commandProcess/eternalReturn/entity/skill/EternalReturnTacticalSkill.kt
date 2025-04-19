package cn.luorenmu.action.commandProcess.eternalReturn.entity.skill

/**
 * @author LoMu
 * Date 2025.04.10 18:03
 */
data class EternalReturnTacticalSkill(
    val tacticalSkills: List<EternalReturnSkill>,
) {
    data class EternalReturnSkill(
        val id: Long,
        val name: String,
        val tooltip: String,
        val imageUrl: String,
    )
}
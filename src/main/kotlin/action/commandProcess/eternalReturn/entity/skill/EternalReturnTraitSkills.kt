package cn.luorenmu.action.commandProcess.eternalReturn.entity.skill

/**
 * @author LoMu
 * Date 2025.04.10 17:48
 * https://er.dakgg.io/api/v1/data/trait-skills?hl=zh-cn
 */
data class EternalReturnTraitSkills(
    val traitSkillGroups: List<EternalReturnSkill>,
    val traitSkills: List<TraitSkill>
){

    data class TraitSkill (
        val id: Long,
        val name: String,
        val tooltip: String,
        val group: String,
        val type: String,
        val imageUrl: String,
        val active: Boolean
    )
}

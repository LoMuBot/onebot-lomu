package cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2025.04.06 15:18
 */
data class EternalReturnMatches(
    val meta: Meta,
    val matches: List<Match>,
) {
    data class Match(
        // 装备
        // https://cdn.dak.gg/assets/er/game-assets/1.44.0/ItemIcon_115504.png
        @JSONField(name = "equipment")
        val equipmentVirtual: Any,
        // 装备背景
        // https://cdn.dak.gg/er/images/item/ico-itemgradebg-04.svg
        val equipmentGrade: List<Int>,
        val userNum: Long,
        val nickname: String,
        val gameId: Long,
        val seasonId: Long,
        val matchingMode: Int,
        val matchingTeamMode: Long,
        val characterNum: Long,
        val skinCode: Long,
        val characterLevel: Long,
        val gameRank: Int,
        val playerKill: Int,
        val playerDeaths: Int,
        val playerAssistant: Int,
        val monsterKill: Long,
        // 这局之前的分数
        val mmrBefore: Int,
        // 这局之后的分数
        val mmrAfter: Int,
        // 这局加了/减了多少分
        val mmrGain: Int,
        // 武器
        // https://er.dakgg.io/api/v1/data/masteries?hl=en
        val bestWeapon: Int,
        val bestWeaponLevel: Int,
        val masteryLevel: Map<String, Long>,
        val versionMajor: Long,
        val versionMinor: Long,
        val serverName: String,
        val criticalStrikeDamage: Long,
        val coolDownReduction: Double,
        val lifeSteal: Double,
        val normalLifeSteal: Double,
        val skillLifeSteal: Long,
        val amplifierToMonster: Double,
        val bonusExp: Long,
        val startDtm: String,
        val duration: Long,
        val playTime: Long,
        val watchTime: Long,
        val totalTime: Long,
        val survivableTime: Long,
        val botAdded: Long,
        val botRemain: Long,
        val restrictedAreaAccelerated: Long,
        val safeAreas: Long,
        val teamNumber: Long,
        val preMade: Long,
        val eventMissionResult: List<Any?>,
        val gainedNormalMmrKFactor: Long,
        val victory: Long,
        val craftUncommon: Long,
        val craftRare: Long,
        val craftEpic: Long,
        val craftLegend: Long,
        val damageToPlayer: Long,
        val damageFromPlayerItemSkill: Long,
        val damageFromPlayerDirect: Long,
        val damageFromPlayerUniqueSkill: Long,
        val healAmount: Long,
        val teamRecover: Long,
        val protectAbsorb: Long,
        val addSurveillanceCamera: Long,
        val addTelephotoCamera: Long,
        val removeSurveillanceCamera: Long,
        val removeTelephotoCamera: Long,
        val useHyperLoop: Long,
        val useSecurityConsole: Long,
        val giveUp: Long,
        val teamSpectator: Long,
        val pcCafe: Long,
        val routeIdOfStart: Long,
        val routeSlotId: Long,
        val placeOfStart: String,
        val matchSize: Long,
        val teamKill: Int,
        val fishingCount: Long,
        val useEmoticonCount: Long,
        val expireDtm: String,
        // 主要天赋技能
        // https://er.dakgg.io/api/v1/data/trait-skills?hl=zh-cn
        val traitFirstCore: Long,
        val rankPoint: Int,
        val scoredPoint: List<Long>,
        val killDetails: String,
        val deathDetails: String,
        val deathsPhaseOne: Long,
        val deathsPhaseTwo: Long,
        val deathsPhaseThree: Long,
        val usedPairLoop: Long,
        val ccTimeToPlayer: Double,
        val creditSource: Map<String, Double>?,
        val boughtInfusion: String,
        val itemTransferredConsole: List<Long>,
        val itemTransferredDrone: List<Long>,
        val escapeState: Long,
        val totalExtraKill: Long,
        val collectItemForLog: List<Long>,
        val equipFirstItemForLog: List<List<Long>>,
        // 战术技能  闪灵、赤色风暴
        //https://er.dakgg.io/api/v1/data/tactical-skills?hl=zh-cn
        val tacticalSkillGroup: Long,
        val tacticalSkillLevel: Long,
        val teamDown: Long,
        val teamBattleZoneDown: Long,
        val teamRepeatDown: Long,
        val skillAmp: Long,
        val isLeavingBeforeCreditRevivalTerminate: Boolean,
        val mmrGainInGame: Long,
        val mmrLossEntryCost: Long,
    ) {
        /**
         * 沟槽的dak.gg返回数据不一致
         */
        private inline fun <reified T> convertToList(value: Any?): List<T> {
            return when (value) {
                is List<*> -> value.mapNotNull { it as? T }
                is Map<*, *> -> value.values.mapNotNull { it as? T }
                else -> emptyList()
            }
        }

        val equipment: List<Int>
            get() {
                return convertToList(equipmentVirtual)
            }
    }


    data class Meta(
        val season: String,
        val matchingMode: String,
        val teamMode: String,
        val page: Long,
        val count: Long,
    )
}

package cn.luorenmu.action.commandProcess.eternalReturn.entity.dto

import com.alibaba.fastjson2.JSON


/**
 * @author LoMu
 * Date 2025.03.29 15:08
 */
data class EternalReturnRender(
    val userNum: Long,
    val nickName: String = "螺母",
    val level: Int = 1,
    val data: EternalReturnPlayerData,
    val profileImageUrl: String? = null,
    val recentPlayers: MutableList<EternalReturnPlayerRecentPlay>,
    val characterUseStats: MutableList<EternalReturnCharacterUseStats>,
    var lomuRating: String? = null,
    val mmrStats: EternalReturnPlayerMMRStats? = null,
    val playTime: Long,
    var rightContent: String = "",
    val season: String,
) {


    data class EternalReturnPlayerMMRStats(
        val mmrDate: List<String>,
        val mmr: List<Int>,
    ) {
        val mmrDateJson = JSON.toJSONString(mmrDate)!!
        val mmrJson: String = JSON.toJSONString(mmr)!!
    }

    data class EternalReturnCharacterUseStats(
        val imgUrl: String,
        val characterName: String,
        val characterPlay: Int,
        val winRate: String,
        val getRP: Int,
        val avgRank: String,
        val avgDmg: Int,
    )

    data class EternalReturnPlayerData(
        var rp: String = "段位鉴定中.",
        var rpName: String = "",
        var tierImageUrl: String = "",
        var play: Int = 0,
        var avgTk: String = "-",
        var avgKill: String = "-",
        var avgRank: String = "-",
        var avgAssists: String = "-",
        var avgDmg: String = "-",
        var top1: String = "-",
        var top2: String = "-",
        var top3: String = "-",
    )

    data class EternalReturnPlayerRecentPlay(
        var imageWrapperUrl: String = "",
        var plays: Int = 1,
        var winRate: String = "0.00%",
        var avgRank: String = "0.00%",
        var nickname: String = "",
        var characterName: String = "",
    )


    data class EternalReturnPlayerMatchData(
        var serverName: String = "",
        var nickName: String = "螺母",
        var characterName: String = "螺母",
        var rank: Int = 8,
        var type: String = "排位",
        var dateHour: String = "25:00",
        var dateMonth: String = "13月13日",
        var characterAvatarUrl: String = "",
        var weaponUrl: String = "",
        var skillUrl: String = "",
        var traitSkillGroupUrl: String = "",
        var traitSkillUrl: String = "",
        var kill: Int = 0,
        var assist: Int = 0,
        var kda: Double = 0.00,
        var dmg: Long = 0,
        var tk: Int = 0,
        var rpChange: Int = 0,
        var rp: Int = 0,
        var rpSvgUrl: String = "",
        var routeId: String = "Private",
        var equips: MutableList<EternalReturnEquip> = mutableListOf(),
        var gameId: String = "0",
        var version: String = "",
        var teamMates: List<EternalReturnTeammate>? = null,
    ) {
        data class EternalReturnTeammate(
            var nickName: String = "",
            var avatarUrl: String = "",
            var rp: String = "0",
            var rpImageUrl: String = "",
            var tk: Int = 0,
            var kill: Int = 0,
            var assist: Int = 0,
            var dmg: Int = 0,
            var weaponUrl: String = "",
            var skillUrl: String = "",
            var traitSkillGroupUrl: String = "",
            var traitSkillUrl: String = "",
            var equips: MutableList<EternalReturnEquip> = mutableListOf(),
        )
    }

}






package cn.luorenmu.action.commandProcess.eternalReturn.entity.dto


/**
 * @author LoMu
 * Date 2025.03.29 15:08
 */
data class EternalReturnRender(
    var nickName: String = "螺母",
    var level: Int = 1,
    var data: EternalReturnPlayerData,
    var profileImageUrl: String? = null,
    var recentPlayContent: String = "",
    var rightContent: String = "",
    var season:String,
    ) {
    data class EternalReturnPlayerData(
        var rp: String = "段位鉴定中.",
        var rpName: String = "",
        var rpRank: String = "",
        var tierImageUrl: String = "",
        var rpLocalRank: String = "",
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
        var item1Url: String = "",
        var item2Url: String = "",
        var item3Url: String = "",
        var item4Url: String = "",
        var item5Url: String = "",
        var itemBg1Url: String = "",
        var itemBg2Url: String = "",
        var itemBg3Url: String = "",
        var itemBg4Url: String = "",
        var itemBg5Url: String = "",
        var gameId: String = "0",
        var version: String = "",
    )

}






package cn.luorenmu.action.render

import action.commandProcess.eternalReturn.entity.EternalReturnCharacterById
import action.commandProcess.eternalReturn.entity.EternalReturnSeasons
import action.commandProcess.eternalReturn.entity.profile.EternalReturnProfile
import action.commandProcess.eternalReturn.entity.profile.EternalReturnProfileStat
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnEquip
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender.EternalReturnPlayerData
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender.EternalReturnPlayerRecentPlay
import cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher.EternalReturnMatches
import cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher.EternalReturnMatchesById
import cn.luorenmu.action.commandProcess.eternalReturn.entity.tier.EternalReturnTiers
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.common.extensions.getUrlIfIndexExists
import cn.luorenmu.common.utils.FreeMarkerUtils
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.core.WebPool
import cn.luorenmu.exception.LoMuBotException
import cn.luorenmu.service.ImageService
import com.mikuac.shiro.common.utils.MsgUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2025.04.22 21:13
 */
@Component
class EternalReturnFindPlayerRender(
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val redisUtils: RedisUtils,
    private val imageService: ImageService,
    private val webPool: WebPool,
    @Value("\${server.port}")
    private val port: String,
) {
    private val log = KotlinLogging.logger { }


    fun imageRenderGenerate(nickname: String): String {
        val pageRender = runBlocking { pageRender(nickname) }
        val userNum = pageRender.userNum

        val imgPath = PathUtils.getEternalReturnNicknameImagePath("render_$userNum")
        val returnMsg = MsgUtils.builder().img(imgPath).build()

        try {
            val parseData = FreeMarkerUtils.parseData("eternal_return_player.ftlh", pageRender)
            log.info { "$nickname 页面图片已生成" }
            redisUtils.setCache("ftlh:eternal_return_player_data_${userNum}", parseData, 5L, TimeUnit.MINUTES)
            webPool.getWebPageScreenshot()
                .screenshotSelector(
                    "http://localhost:$port/ftlh/eternal_return_player_data_${userNum}",
                    imgPath,
                    "#content-container"
                )
            redisUtils.setCache("nickname:${nickname}", returnMsg, 5L, TimeUnit.MINUTES)
            return returnMsg
        } catch (e: Exception) {
            throw LoMuBotException("无法为其生成数据 -> $nickname")
        }
    }

    suspend fun pageRender(nickname: String): EternalReturnRender {
        val currentSeason = eternalReturnRequestData.currentSeason()?.currentSeason
        val currentSeasonKey = currentSeason?.key ?: run {
            throw LoMuBotException("无法获取当前赛季")
        }
        val profile = eternalReturnRequestData.profile(nickname, currentSeasonKey)
        val tiers = eternalReturnRequestData.tiers()
        // 最新活跃赛季 由于过去赛季的api数据不同 因此暂时不支持处理
        //val season =
        //    eternalReturnRequestData.currentSeason()!!.seasons.first { seasons -> seasons.id == profile.playerSeasons.maxBy { it.seasonId }.seasonId }
        val matches = eternalReturnRequestData.matches(nickname, currentSeasonKey)
        if (profile == null || tiers == null || matches == null) {
            throw LoMuBotException("多次尝试仍然无法从dak.gg获取数据")
        }

        //必要数据由left优先生成并渲染
        val eternalReturnRender = pageLeftConvert(profile, tiers, currentSeason)
        pageRightConvert(matches, eternalReturnRender)
        eternalReturnRender.lomuRating = matchRating(matches)
        return eternalReturnRender

    }


    suspend fun pageLeftConvert(
        profile: EternalReturnProfile,
        tiers: EternalReturnTiers,
        season: EternalReturnSeasons?,
    ): EternalReturnRender {
        val player = profile.player
        val playerSeasons = profile.playerSeasons
        val playerSeasonOverviews = profile.playerSeasonOverviews
        val recentPlays = mutableListOf<EternalReturnPlayerRecentPlay>()
        val characterUseStats = mutableListOf<EternalReturnRender.EternalReturnCharacterUseStats>()
        var profileImageUrl: String? = null
        var playerMMRStats: EternalReturnRender.EternalReturnPlayerMMRStats? = null
        var playTime: Long = 0
        val eternalReturnPlayerData = EternalReturnPlayerData().apply {
            if (playerSeasons.isNotEmpty()) {
                // 选取最新的段位信息 并且装配部分数据
                playerSeasons.firstOrNull { it.seasonId == season?.id }?.let { data ->
                    val currentTier = data.tierId.let { tierID -> tiers.tiers.first { it.id == tierID } }
                    // example: 1234RP or 段位鉴定中.
                    if (data.mmr != 0) {
                        rp = data.mmr.let { mmr -> mmr.toString() + "RP" }
                    }
                    // example: 灭钻 2 - 209  无暇 -209
                    if (currentTier.id != 0) {
                        val tierGrad = if (data.tierId > 6 && data.tierId * 10 > 60) "" else data.tierGradeId
                        rpName = "${currentTier.name}$tierGrad - ${data.tierMmr}RP"
                    }
                    tierImageUrl = getTierImgUrl(currentTier.id)

                }



                playerSeasonOverviews?.let { seasonOverviews ->
                    val firstSeasonOverview = seasonOverviews.firstOrNull()

                    //筛选出排位信息
                    seasonOverviews.firstOrNull { seasonOverview -> seasonOverview.matchingModeId == 3 }
                        ?.let { seasonOverview ->
                            play = seasonOverview.play
                            val playDouble = play.toDouble()
                            if (play != 0) {
                                avgTk = String.format("%.2f", seasonOverview.teamKill / playDouble)
                                avgKill = String.format("%.2f", seasonOverview.playerKill / playDouble)
                                avgAssists = String.format("%.2f", seasonOverview.playerAssistant / playDouble)
                                avgDmg = (seasonOverview.damageToPlayer / play).toString()
                                avgRank = "#" + String.format("%.2f", seasonOverview.place / playDouble)
                                top1 = String.format("%.1f", (seasonOverview.win / playDouble) * 100) + "%"
                                top2 = String.format("%.1f", (seasonOverview.top2 / playDouble) * 100) + "%"
                                top3 = String.format("%.1f", (seasonOverview.top3 / playDouble) * 100) + "%"
                            }
                        }


                    //主页图
                    profileImageUrl = firstSeasonOverview?.characterStats?.maxByOrNull(
                        EternalReturnProfileStat::play
                    )
                        ?.let { stats ->
                            getCharacterImgUrl(
                                EternalReturnCharacterById.CharacterImgUrlType.ResultImageUrl,
                                stats.key.toInt(),
                                stats.skinStats?.maxByOrNull(EternalReturnProfileStat::play)?.key ?: -1L
                            )
                        }

                    //近期一起玩的人
                    seasonOverviews.firstOrNull { seasonOverview -> seasonOverview.duoStats.isNotEmpty() }
                        ?.let { seasonOverview ->
                            seasonOverview.duoStats.take(8).forEach { duoStat ->
                                recentPlays.add(EternalReturnPlayerRecentPlay().apply {
                                    imageWrapperUrl = getCharacterImgUrl(
                                        EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl,
                                        duoStat.characterStats.first().key.toInt(),
                                    )
                                    this.plays = duoStat.play
                                    val playDouble = this.plays.toDouble()
                                    this.nickname = duoStat.nickname

                                    this.winRate = "${String.format("%.1f", (duoStat.win / playDouble) * 100)}%"
                                    this.avgRank = "#${String.format("%.1f", duoStat.place / playDouble)}"
                                })
                            }
                        }

                    // 常用排位角色
                    seasonOverviews.firstOrNull { it.matchingModeId == 3 }?.characterStats?.take(8)
                        ?.forEach { characterState ->
                            val character = eternalReturnRequestData.getCharacterInfo(characterState.key.toString())
                            characterUseStats.add(
                                EternalReturnRender.EternalReturnCharacterUseStats(
                                    characterName = character.name,
                                    imgUrl = getCharacterImgUrl(
                                        EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl,
                                        characterState.key.toInt()
                                    ),
                                    winRate = "${
                                        String.format(
                                            "%.1f",
                                            if (characterState.win == 0L) 0.0 else characterState.win / characterState.play.toDouble() * 100
                                        )
                                    }%",
                                    characterPlay = characterState.play,
                                    getRP = characterState.mmrGain,
                                    avgRank = "#${
                                        String.format(
                                            "%.1f",
                                            characterState.place / characterState.play.toDouble()
                                        )
                                    }",
                                    avgDmg = if (characterState.damageToPlayer == 0) 0 else characterState.damageToPlayer / characterState.play,
                                )
                            )
                        }

                    // 分数曲线
                    firstSeasonOverview?.let { seasonOverview ->
                        if (seasonOverview.mmrStats.isNotEmpty()) {
                            val mmrStats = seasonOverview.mmrStats.take(7).reversed()
                            playerMMRStats = EternalReturnRender.EternalReturnPlayerMMRStats(
                                mmrDate = mmrStats.map { mmrs ->
                                    val dateStr = mmrs.first().toString().substring(4)
                                    dateStr.substring(0, 2) + "/" + dateStr.substring(2)
                                },
                                mmr = mmrStats.map { mmrs -> mmrs[1] }
                            )
                        }
                    }

                    // 游戏时间、单位为秒
                    playTime = firstSeasonOverview?.playTime ?: 0
                }
            }
        }



        return EternalReturnRender(
            userNum = player.userNum,
            nickName = player.name,
            level = player.accountLevel,
            eternalReturnPlayerData,
            profileImageUrl,
            mmrStats = playerMMRStats,
            recentPlayers = recentPlays,
            season = season?.name ?: "未知赛季",
            playTime = playTime,
            characterUseStats = characterUseStats
        )
    }

    private suspend fun matcherConvert(
        match: EternalReturnMatches.Match,
        dateFormatter: DateTimeFormatter,
        teammate: EternalReturnMatchesById?,
    ): String {
        val matcherData = EternalReturnRender.EternalReturnPlayerMatchData().apply {
            type = match.matchTypeStr
            rank = if (match.escapeState == 3) 99 else match.gameRank
            gameId = match.gameId.toString()
            serverName = match.serverName
            version = "1.${match.versionMajor}.${match.versionMinor}"
            kill = match.playerKill
            assist = match.playerAssistant
            dmg = match.damageToPlayer
            tk = match.teamKill
            rp = match.mmrAfter
            rpChange = match.mmrGain
            val killAndAssist = kill + assist
            kda =
                if (match.playerDeaths == 0) killAndAssist.toDouble() else killAndAssist.toDouble() / match.playerDeaths
            routeId = if (match.routeIdOfStart != 0L) match.routeIdOfStart.toString() else "Private"
            val date = ZonedDateTime.parse(match.startDtm, dateFormatter)
            dateHour = "${date.hour}:${date.minute}:${date.second}"
            dateMonth = "${date.monthValue}月${date.dayOfMonth}日"

            skillUrl = getTacticalSkillImgUrl(match.tacticalSkillGroup)
            traitSkillUrl = getTraitSkillImgUrl(match.traitFirstCore)
            traitSkillGroupUrl = getTraitSkillImgUrl(match.traitSecondSub.first(), true)
            equips = equipmentConvert(match.equipment.map { it.toLong() }.toList(), match.equipmentGrade)
            characterAvatarUrl =
                getCharacterImgUrl(
                    EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl,
                    match.characterNum.toInt(),
                    match.skinCode
                )
            characterName =
                eternalReturnRequestData.getCharacterInfo(match.characterNum.toString()).name
            weaponUrl = getWeaponImgUrl(match.bestWeapon)


            // 队友
            val teammateInfos: MutableList<EternalReturnRender.EternalReturnPlayerMatchData.EternalReturnTeammate>? =
                teammate?.let { teamMateDataConvert(teammate, match.nickname) }

            teamMates = teammateInfos
        }
        return FreeMarkerUtils.parseData("eternal_return_war_record.ftlh", matcherData)
    }

    /**
     * 装备转换
     */
    private fun equipmentConvert(equipment: List<Long>, equipmentGrade: List<Int>): MutableList<EternalReturnEquip> {
        val equips: MutableList<EternalReturnEquip> = mutableListOf()
        for (i in 0 until 5) {
            equips.add(
                i,
                EternalReturnEquip(
                    itemUrl = if (i < equipment.size) getItemImgUrl(equipment[i]) else "",
                    itemBgUrl = if (i < equipmentGrade.size) getItemImgBgUrl(equipmentGrade[i]) else ""
                )
            )
        }
        return equips
    }

    /**
     * 队友数据转换
     */
    private fun teamMateDataConvert(
        teammate: EternalReturnMatchesById,
        nickname: String,
    ): MutableList<EternalReturnRender.EternalReturnPlayerMatchData.EternalReturnTeammate> {
        val teammateInfos: MutableList<EternalReturnRender.EternalReturnPlayerMatchData.EternalReturnTeammate> =
            mutableListOf()
        teammate.let {
            val selfInfo = teammate.matches.first { matchById -> matchById.nickname == nickname }
            val teamNumber = selfInfo.teamNumber
            val teamMates =
                teammate.matches.filter { matchById -> matchById.teamNumber == teamNumber && matchById.nickname != nickname }

            teamMates.forEach { teamMate ->
                teammateInfos.add(
                    EternalReturnRender.EternalReturnPlayerMatchData.EternalReturnTeammate().apply {
                        nickName = teamMate.nickname
                        avatarUrl = getCharacterImgUrl(
                            EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl,
                            teamMate.characterNum.toInt(),
                            teamMate.skinCode
                        )
                        dmg = teamMate.damageToPlayer.toInt()
                        kill = teamMate.playerKill
                        assist = teamMate.playerAssistant
                        tk = teamMate.teamKill
                        rpImageUrl =
                            getTierImgUrl(teammate.playerTiers.first { iter -> iter.userNum.toLong() == teamMate.userNum }.tierId)
                        rp = teamMate.mmrAfter.toString()
                        skillUrl = getTacticalSkillImgUrl(teamMate.tacticalSkillGroup)
                        traitSkillUrl = getTraitSkillImgUrl(teamMate.traitFirstCore)
                        traitSkillGroupUrl = getTraitSkillImgUrl(teamMate.traitSecondSub.first(), true)
                        weaponUrl = getWeaponImgUrl(teamMate.bestWeapon)
                        equips =
                            equipmentConvert(teamMate.equipment.map { it.toLong() }.toList(), teamMate.equipmentGrade)
                    })
            }
        }
        return teammateInfos
    }


    /**
     * 对局评价
     */
    private fun matchRating(matches: EternalReturnMatches): String? {
        val asia2Count = matches.matches.count { it.serverName == "Asia2" }
        val asia2Rank1Or2Count =
            matches.matches.count { it.serverName == "Asia2" && (it.gameRank == 1 || it.gameRank == 2) }
        if (asia2Count > 15) {
            if (asia2Rank1Or2Count > asia2Count / 2) {
                return "全是亚二 而且胜率还这么高? 这绝对是炸鱼(ﾟдﾟ)"
            } else if (asia2Rank1Or2Count in 0..2) {
                return "亚二全是炸鱼哥 快跑! (╯°Д°)╯ ┻━┻"
            }
        }

        if (matches.matches.count { it.matchTypeStr == "钴协议" } > 10) {
            return "全是钴协议喵 (⁰▿⁰)"
        }
        val notGuardMatches = matches.matches.filter { it.matchTypeStr != "钴协议" }
        val totalMatches = notGuardMatches.count { it.matchTypeStr != "钴协议" }
        val rankMatches = notGuardMatches.filter { it.matchTypeStr == "排位" }
        if (totalMatches > 10) {
            val rank1Count = notGuardMatches.count { it.gameRank == 1 }
            val rank2Count = notGuardMatches.count { it.gameRank == 2 }
            val rank3Count = notGuardMatches.count { it.gameRank == 3 }
            val highDmg = notGuardMatches.maxOfOrNull { it.damageToPlayer } ?: 0
            val lowDmg = notGuardMatches.minOfOrNull { it.damageToPlayer } ?: 0
            val avgDmg = notGuardMatches.sumOf { it.damageToPlayer } / notGuardMatches.size
            val rankLastCount = notGuardMatches.count { it.gameRank == it.squadRumbleRank && it.squadRumbleRank != 2 }
            return when {
                rank1Count > totalMatches * 0.6 -> "太强了！简直就是炸鱼 (ﾉ≧∀≦)ﾉ"
                rank3Count > totalMatches / 2 -> "怎么总是老三,谁的问题!! (／‵Д′)／~ ╧╧"
                rank1Count == totalMatches -> "这绝对是炸鱼! 请务必带上我 (๑•̀ㅂ•́)و✧"
                rankLastCount > totalMatches * 0.5 -> "垫底次数有点多啊 加油吧( ´･･)ﾉ(._.`)"
                rank1Count == 0 && rank2Count == 0 -> "全灰! 这是怎么做到的?  (╬ﾟдﾟ)"
                rank1Count == totalMatches - 1 -> "差一局就全胜? 大佬能带带我吗 我也想体验炸鱼的滋味(☉д⊙)"
                rank1Count == rank2Count && rank2Count == rank3Count && rank1Count != 1 -> "胜负分布很均匀呢 (￣ω￣;)"
                rank1Count + rank2Count == 1 -> "嗯嗯 真是相当糟糕的战绩呢(･ω･)"
                rank1Count == 0 && rank2Count == totalMatches -> "万年老二...总是差那么一点 队友别浪了!(´；ω；｀)"
                avgDmg >= 25000 -> "(ﾉ≧∇≦)ﾉ 这均伤简直离谱! 你玩的是亚二吗???"
                avgDmg in 7000..7999 -> "(´･_･`) 要多练习输出手法呢 团战也要适当拉扯和走位呢"
                avgDmg <= 5000 -> "这输出(´-﹏-`；) 你玩的是约翰吗？"
                highDmg >= 30000 && lowDmg <= 2000 -> "发挥不稳定呢(。-`ω´-) 时而超神时而超鬼光速下机"
                else -> if (rankMatches.size > 10) {

                    val mmrState = rankMatches.sumOf { it.mmrGain }
                    when {
                        mmrState < -400 -> "恭喜您，马上就回到属于你的段位了\uD83E\uDD23\uD83E\uDD23"
                        mmrState < -200 -> "哎哟我去，您这是反向冲分啊？建议改ID：『慈善家』🤡"
                        mmrState < -50 -> "${rankMatches.size}场 掉了${-mmrState}分？ \uD83E\uDD23\uD83E\uDD23\uD83E\uDD23"
                        mmrState < 0 -> "打了这么多把分还掉了？您这是来搞笑的吗？😅"
                        mmrState == 0 -> "忙活半天原地踏步+0分 您是搁这打维护还是您搁这养生呢？🛌"
                        mmrState in 1..10 -> "${rankMatches.size}场+$mmrState 分？...您这还不如玩个跑的快的苟到第三名去逃生？\uD83D\uDE05"
                        mmrState in 11..50 -> "可以可以，至少加分了，对你来说已经非常棒了\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F"
                        mmrState in 51..200 -> "美好的一天从小分开始加起喵~(。-`ω´-) "
                        mmrState in 201..300 -> "段位提升了呢!\uD83D\uDC4D"
                        mmrState > 301 -> "哇哦 好厉害 加了这么多分(★ω★)"
                        else -> "o.O?"
                    }
                } else "o.O?"
            }
        }
        return null
    }


    private suspend fun pageRightConvert(
        matches: EternalReturnMatches,
        eternalReturnRender: EternalReturnRender,
    ) {
        val rightContent = StringBuilder()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        coroutineScope {
            val firstMatchId = matches.matches.firstOrNull { match -> match.matchTypeStr == "排位" }?.gameId
            val deferredResults = matches.matches
                .map { match ->
                    try {
                        val teammate = firstMatchId?.let {
                            if (match.gameId == firstMatchId) {
                                val seasonID =
                                    eternalReturnRequestData.currentSeason()?.seasons?.first { sea -> sea.key == matches.meta.season }?.id
                                eternalReturnRequestData.getMatchesById(
                                    match.gameId.toString(),
                                    match.nickname,
                                    seasonID ?: 0
                                )
                            } else null
                        }
                        matcherConvert(match, dateFormatter, teammate)
                    } catch (e: Exception) {
                        log.error { e.printStackTrace() }
                    }
                }
            deferredResults.forEach { result ->
                rightContent.append(result)
            }
        }
        eternalReturnRender.rightContent = rightContent.toString()
    }


    private fun getCharacterImgUrl(type: EternalReturnCharacterById.CharacterImgUrlType, id: Int, skin: Long = -1) =
        run {
            imageService.getEternalReturnCharacterImage(type, id, skin)
            "/images/eternal_return/character/$type/$id/$skin"
        }

    private fun getItemImgUrl(id: Long) = run {
        imageService.getEternalReturnItemImage(id)
        "/images/eternal_return/item/${id}"
    }

    private fun getTierImgUrl(id: Int) = run {
        imageService.getTierImage(id)
        "/images/eternal_return/tier/${id}"
    }

    private fun getItemImgBgUrl(id: Int) = run {
        imageService.getEternalReturnItemBgImage(id)
        "/images/eternal_return/item_bg/${id}"
    }

    private fun getTraitSkillImgUrl(id: Long, `is`: Boolean = false) = run {

        imageService.getEternalReturnTraitSkillImage(id)
        "/images/eternal_return/trait_skill/${id}?is=${`is`}"

    }

    private fun getTacticalSkillImgUrl(id: Long) = run {
        imageService.getEternalReturnTacticalSkillImage(id)
        "/images/eternal_return/tactical_skill/${id}"
    }

    private fun getWeaponImgUrl(id: Int) = run {
        imageService.getEternalReturnWeaponImage(id)
        "/images/eternal_return/weapon/${id}"
    }


}
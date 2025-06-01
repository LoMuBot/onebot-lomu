package cn.luorenmu.action.render

import action.commandProcess.eternalReturn.entity.EternalReturnCharacterById
import action.commandProcess.eternalReturn.entity.EternalReturnSeasons
import action.commandProcess.eternalReturn.entity.profile.EternalReturnProfile
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender.EternalReturnPlayerData
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender.EternalReturnPlayerRecentPlay
import cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher.EternalReturnMatches
import cn.luorenmu.action.commandProcess.eternalReturn.entity.tier.EternalReturnTiers
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.common.extensions.getUrlIfIndexExists
import cn.luorenmu.common.utils.FreeMarkerUtils
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.common.utils.WkhtmltoimageUtils
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
    @Value("\${server.port}")
    private val port: String,
) {
    private val log = KotlinLogging.logger { }


    fun imageRenderGenerate(nickname: String): String {
        val pageRender = runBlocking { pageRender(nickname) }
        val userNum = pageRender.userNum
        val parseData = FreeMarkerUtils.parseData("eternal_return_player.ftlh", pageRender)
        val imgPath = PathUtils.getEternalReturnNicknameImagePath("render_$userNum")
        val returnMsg = MsgUtils.builder().img(imgPath).build()
        redisUtils.setCache("ftlh:eternal_return_player_data_${userNum}", parseData, 5L, TimeUnit.MINUTES)
        WkhtmltoimageUtils.convertHtmlToImage(
            "http://localhost:$port/ftlh/eternal_return_player_data_${userNum}", imgPath, mapOf("zoom" to "1")
        )
        log.info { "$nickname 页面图片已生成" }
        return returnMsg
    }

    suspend fun pageRender(nickname: String): EternalReturnRender {
        val currentSeason = eternalReturnRequestData.currentSeason()?.currentSeason
        val currentSeasonKey = currentSeason?.key ?: "SEASON_16"
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
                    //筛选出排位信息
                    seasonOverviews.firstOrNull { it.matchingModeId == 3 }?.let { seasonOverview ->
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
                    profileImageUrl = seasonOverviews.firstOrNull()?.characterStats?.firstOrNull()?.let { stats ->
                        getCharacterImgUrl(
                            EternalReturnCharacterById.CharacterImgUrlType.ResultImageUrl,
                            stats.key.toInt(),
                            stats.skinStats?.firstOrNull()?.key ?: -1L
                        )
                    }


                    seasonOverviews.firstOrNull()?.let { seasonOverview ->
                        //近期一起玩的人
                        seasonOverview.duoStats.forEach { duoStat ->
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
                        // 常用排位角色
                        seasonOverview.characterStats.take(10).forEach { characterState ->
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
                    }
                }
            }
        }

        val recentPlayContent = StringBuilder()
        if (recentPlays.isNotEmpty()) {
            for (recentPlay in recentPlays) {
                recentPlayContent.append(FreeMarkerUtils.parseData("eternal_return_recent_play.ftlh", recentPlay))
            }
        }

        return EternalReturnRender(
            userNum = player.userNum,
            nickName = player.name,
            level = player.accountLevel,
            eternalReturnPlayerData,
            profileImageUrl,
            recentPlayContent = recentPlayContent.toString(),
            season = season?.name ?: "未知赛季",
            characterUseStats = characterUseStats
        )
    }

    private suspend fun matcherConvert(
        match: EternalReturnMatches.Match,
        dateFormatter: DateTimeFormatter,
    ): String {
        val matcherData = EternalReturnRender.EternalReturnPlayerMatchData().apply {
            type = if (match.matchingMode == 3) "排位" else "匹配"
            rank = if (match.escapeState == 3.toLong()) 99 else match.gameRank
            gameId = match.gameId.toString()
            serverName = match.serverName
            version = "1.${match.versionMajor}.${match.versionMinor}"
            kill = match.playerKill
            assist = match.playerAssistant
            dmg = match.damageToPlayer
            tk = match.teamKill
            rp = match.mmrAfter
            rpChange = match.mmrGain
            kda = if (match.playerDeaths == 0) kill.toDouble() else kill.toDouble() / match.playerDeaths
            routeId = if (match.routeIdOfStart != 0L) match.routeIdOfStart.toString() else "Private"
            val date = ZonedDateTime.parse(match.startDtm, dateFormatter)
            dateHour = "${date.hour}:${date.minute}:${date.second}"
            dateMonth = "${date.monthValue}月${date.dayOfMonth}日"

            skillUrl = getTacticalSkillImgUrl(match.tacticalSkillGroup)
            traitSkillUrl = getTraitSkillImgUrl(match.traitFirstCore)
            traitSkillGroupUrl = getTraitSkillImgUrl(match.traitFirstCore, true)

            itemBg1Url = match.equipmentGrade.getUrlIfIndexExists(0) { getItemImgBgUrl(match.equipmentGrade[it]) }
            itemBg2Url = match.equipmentGrade.getUrlIfIndexExists(1) { getItemImgBgUrl(match.equipmentGrade[it]) }
            itemBg3Url = match.equipmentGrade.getUrlIfIndexExists(2) { getItemImgBgUrl(match.equipmentGrade[it]) }
            itemBg4Url = match.equipmentGrade.getUrlIfIndexExists(3) { getItemImgBgUrl(match.equipmentGrade[it]) }
            itemBg5Url = match.equipmentGrade.getUrlIfIndexExists(4) { getItemImgBgUrl(match.equipmentGrade[it]) }

            val equipment = match.equipment.map { it.toLong() }.toList()
            item1Url = match.equipment.getUrlIfIndexExists(0) { getItemImgUrl(equipment[it]) }
            item2Url = match.equipment.getUrlIfIndexExists(1) { getItemImgUrl(equipment[it]) }
            item3Url = match.equipment.getUrlIfIndexExists(2) { getItemImgUrl(equipment[it]) }
            item4Url = match.equipment.getUrlIfIndexExists(3) { getItemImgUrl(equipment[it]) }
            item5Url = match.equipment.getUrlIfIndexExists(4) { getItemImgUrl(equipment[it]) }


            characterAvatarUrl =
                getCharacterImgUrl(
                    EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl,
                    match.characterNum.toInt(),
                    match.skinCode
                )
            characterName =
                eternalReturnRequestData.getCharacterInfo(match.characterNum.toString()).name
            weaponUrl = getWeaponImgUrl(match.bestWeapon)
        }
        val parseData = FreeMarkerUtils.parseData("eternal_return_war_record.ftlh", matcherData)


        return FreeMarkerUtils.parseData(
            "eternal_return_war_record_small.ftlh",
            //小于三 使用不同的方式渲染
            mapOf("content" to parseData, "top" to if (matcherData.rank < 3) matcherData.rank else 3)
        )

    }


    private suspend fun pageRightConvert(
        matches: EternalReturnMatches,
        eternalReturnRender: EternalReturnRender,
    ) {
        val rightContent = StringBuilder()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        coroutineScope {
            val deferredResults = matches.matches
                .filter { it.matchingMode == 3 || it.matchingMode == 2 }
                .map { match ->
                    matcherConvert(match, dateFormatter)
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
            "http://localhost:$port/images/eternal_return/character/$type/$id/$skin"
        }

    private fun getItemImgUrl(id: Long) = run {
        imageService.getEternalReturnItemImage(id)
        "http://localhost:$port/images/eternal_return/item/${id}"
    }

    private fun getTierImgUrl(id: Int) = run {
        imageService.getTierImage(id)
        "http://localhost:$port/images/eternal_return/tier/${id}"
    }

    private fun getItemImgBgUrl(id: Int) = run {
        imageService.getEternalReturnItemBgImage(id)
        "http://localhost:$port/images/eternal_return/item_bg/${id}"
    }

    private fun getTraitSkillImgUrl(id: Long, `is`: Boolean = false) = run {
        imageService.getEternalReturnTraitSkillImage(id)
        "http://localhost:$port/images/eternal_return/trait_skill/${id}?is=${`is`}"
    }

    private fun getTacticalSkillImgUrl(id: Long) = run {
        imageService.getEternalReturnTacticalSkillImage(id)
        "http://localhost:$port/images/eternal_return/tactical_skill/${id}"
    }

    private fun getWeaponImgUrl(id: Int) = run {
        imageService.getEternalReturnWeaponImage(id)
        "http://localhost:$port/images/eternal_return/weapon/${id}"
    }

}
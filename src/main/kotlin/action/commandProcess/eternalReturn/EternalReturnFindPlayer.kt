package cn.luorenmu.action.commandProcess.eternalReturn

import action.commandProcess.eternalReturn.entity.EternalReturnCharacterById
import action.commandProcess.eternalReturn.entity.profile.EternalReturnProfile
import cn.hutool.http.HttpException
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender.EternalReturnPlayerData
import cn.luorenmu.action.commandProcess.eternalReturn.entity.dto.EternalReturnRender.EternalReturnPlayerRecentPlay
import cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher.EternalReturnMatches
import cn.luorenmu.action.commandProcess.eternalReturn.entity.tier.EternalReturnTiers
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.utils.FreeMarkerUtils
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.common.utils.WkhtmltoimageUtils
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.service.ImageService
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2025.01.28 13:42
 */
@Component("eternalReturnFindPlayers")
class EternalReturnFindPlayer(
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val redisUtils: RedisUtils,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
    private val redisTemplate: StringRedisTemplate,
    private val botContainer: BotContainer,
    private val imageService: ImageService,
    @Value("\${server.port}")
    private val port: String,
) : CommandProcess {
    private val log = KotlinLogging.logger { }

    override fun process(command: String, sender: MessageSender): String? {
        var nickname =
            sender.message.replaceAtToEmpty(sender.botId).trim()
                .replace(Regex(command), "")
                .replaceBlankToEmpty()
                .lowercase()


        // check name rule
        if (nickname.isBlank()) {
            nickname = sender.senderName
        }
        if (nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        val opsForValue = redisTemplate.opsForValue()

        // check cache
        opsForValue["Eternal_Return_NickName:$nickname"]?.let {
            return "$it \n该数据由缓存命中"
        }

        // check name exist and sync data
        if (!eternalReturnRequestData.checkPlayerExists(nickname)) {
            return MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
        }
        eternalReturnRequestData.syncPlayers(nickname)
        botContainer.getFirstBot().setMsgEmojiLike(sender.messageId.toString(), "124")
        imageRenderGenerate(nickname)
        return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)

    }

    @Async
    fun imageRenderGenerate(nickname: String) {
        try {
            val pageRender = runBlocking { pageRender(nickname) }
            val parseData = FreeMarkerUtils.parseData("eternal_return_player.ftlh", pageRender)
            val imgPath = PathUtils.getEternalReturnNicknameImagePath(nickname)

            val returnMsg = MsgUtils.builder().img(OneBotMedia().file(imgPath).cache(false).proxy(false)).build()
            redisUtils.setCache("ftlh:eternal_return_player_data_${nickname}", parseData, 5L, TimeUnit.MINUTES)
            WkhtmltoimageUtils.convertHtmlToImage(
                "http://localhost:$port/ftlh/eternal_return_player_data_${nickname}", imgPath, mapOf("zoom" to "2")
            )
        } catch (e: Exception) {
            log.error { e.printStackTrace() }
        }
    }

    suspend fun pageRender(nickname: String): EternalReturnRender {
        val currentSeason = eternalReturnRequestData.currentSeason()?.currentSeason?.key ?: "SEASON_16"
        val profile = eternalReturnRequestData.profile(nickname, currentSeason)
        val tiers = eternalReturnRequestData.tiers()
        // 最新活跃赛季 由于过去赛季的api数据不同 因此暂时不支持处理
        //val season =
        //    eternalReturnRequestData.currentSeason()!!.seasons.first { seasons -> seasons.id == profile.playerSeasons.maxBy { it.seasonId }.seasonId }
        val matches = eternalReturnRequestData.matches(nickname, currentSeason)
        if (profile == null || tiers == null || matches == null) {
            throw HttpException("多次尝试仍然无法从dak.gg获取数据")
        }

        //必要数据由left优先生成并渲染
        val eternalReturnRender = pageLeftConvert(profile, tiers)
        pageRightConvert(matches, eternalReturnRender)
        log.info { "$nickname 页面已生成" }
        return eternalReturnRender

    }


    suspend fun pageLeftConvert(
        profile: EternalReturnProfile,
        tiers: EternalReturnTiers,
    ): EternalReturnRender {
        val player = profile.player
        val playerSeasons = profile.playerSeasons
        val playerSeasonOverviews = profile.playerSeasonOverviews
        val recentPlays = mutableListOf<EternalReturnPlayerRecentPlay>()
        var profileImageUrl: String? = null
        val eternalReturnPlayerData = EternalReturnPlayerData().apply {
            if (playerSeasons.isNotEmpty()) {
                // 选取最新的段位信息 并且装配部分数据
                playerSeasons.maxBy { it.seasonId }.let { data ->
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
                    tierImageUrl = ""

                }

                playerSeasonOverviews?.let { seasonOverviews ->
                    //筛选出排位信息
                    val df = DecimalFormat("#.##")
                    val rankDf = DecimalFormat("#.#")
                    seasonOverviews.firstOrNull { it.matchingModeId == 3 }?.let { seasonOverview ->
                        play = seasonOverview.play
                        val playDouble = play.toDouble()
                        if (play != 0) {
                            avgTk = df.format(seasonOverview.teamKill / playDouble).toString()
                            avgKill = df.format(seasonOverview.playerKill / playDouble).toString()

                            avgAssists = df.format(seasonOverview.playerAssistant / playDouble).toString()
                            avgDmg = "${seasonOverview.damageToPlayer / play}"
                            avgRank = "#${df.format(seasonOverview.place / playDouble)}"
                            top1 = "${rankDf.format((seasonOverview.win / playDouble) * 100)}%"
                            top2 = "${rankDf.format((seasonOverview.top2 / playDouble) * 100)}%"
                            top3 = "${rankDf.format((seasonOverview.top3 / playDouble) * 100)}%"
                        }
                    }
                    // 排名
                    seasonOverviews.firstOrNull { it.matchingModeId == 3 && it.rank != null }?.let { seasonOverview ->
                        val server = seasonOverview.serverStats.first().key
                        // 在Seoul服务器.共31313131人-排名第123123
                        seasonOverview.rank?.in1000?.let { in1000 ->
                            rpRank = "全球服务器-${in1000.rank}"
                            rpLocalRank = "${server}服务器.${seasonOverview.rank?.global?.rank}"
                        } ?: run {
                            rpLocalRank =
                                "${server}服务器.共${seasonOverview.rank?.local?.rankSize}-${seasonOverview.rank?.local?.rank}"
                            rpRank =
                                "全球服务器.共${seasonOverview.rank?.global?.rankSize}-${seasonOverview.rank?.global?.rank}"
                        }
                        seasonOverview.characterStats.firstOrNull()?.let { stats ->
                            profileImageUrl =
                                getCharacterImgUrl(EternalReturnCharacterById.UrlType.ResultImageUrl, stats.key.toInt())

                        }


                    }

                    //近期一起玩的人
                    seasonOverviews.firstOrNull { it.duoStats.isNotEmpty() }?.let { seasonOverview ->
                        seasonOverview.duoStats.forEach { duoStat ->
                            recentPlays.add(EternalReturnPlayerRecentPlay().apply {
                                imageWrapperUrl = getCharacterImgUrl(
                                    EternalReturnCharacterById.UrlType.CommunityImageUrl,
                                    duoStat.characterStats.first().key.toInt()
                                )
                                this.plays = duoStat.play
                                val playDouble = this.plays.toDouble()
                                this.nickname = duoStat.nickname

                                this.winRate = "${rankDf.format((duoStat.win / playDouble) * 100)}%"
                                this.avgRank = "#${df.format(duoStat.place / playDouble)}"
                            })
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
            nickName = player.name,
            level = player.accountLevel,
            eternalReturnPlayerData,
            profileImageUrl,
            recentPlayContent = recentPlayContent.toString()
        )
    }

    private suspend fun matcherConvert(
        match: EternalReturnMatches.Match,
        dateFormatter: DateTimeFormatter,
    ): String {
        val matcherData = EternalReturnRender.EternalReturnPlayerMatchData().apply {
            type = if (match.matchingMode == 3) "排位" else "匹配"
            rank = match.gameRank
            gameId = match.gameId.toString()
            version = "1.${match.versionMajor}.${match.versionMinor}"
            kill = match.playerKill
            assist = match.playerAssistant
            dmg = match.damageToPlayer
            tk = match.teamKill
            kda = if (match.playerDeaths == 0) kill.toDouble() else kill.toDouble() / match.playerDeaths
            routeId = if (match.routeIdOfStart != 0L) match.routeIdOfStart.toString() else "Private"
            val date = ZonedDateTime.parse(match.startDtm, dateFormatter)
            dateHour = "${date.hour}:${date.minute}:${date.second}"
            dateMonth = "${date.monthValue}月${date.dayOfMonth}日"

            skillUrl = getTacticalSkillImgUrl(match.tacticalSkillGroup)
            traitSkillUrl = getTraitSkillImgUrl(match.traitFirstCore)
            traitSkillGroupUrl = getTraitSkillImgUrl(match.traitFirstCore, true)

            itemBg1Url = getItemImgBgUrl(match.equipmentGrade[0])
            itemBg2Url = getItemImgBgUrl(match.equipmentGrade[1])
            itemBg3Url = getItemImgBgUrl(match.equipmentGrade[2])
            itemBg4Url = getItemImgBgUrl(match.equipmentGrade[3])
            itemBg5Url = getItemImgBgUrl(match.equipmentGrade[4])

            item1Url = getItemImgUrl(match.equipment[0])
            item2Url = getItemImgUrl(match.equipment[1])
            item3Url = getItemImgUrl(match.equipment[2])
            item4Url = getItemImgUrl(match.equipment[3])
            item5Url = getItemImgUrl(match.equipment[4])
            characterAvatarUrl =
                getCharacterImgUrl(EternalReturnCharacterById.UrlType.CommunityImageUrl, match.characterNum.toInt())
            characterName =
                eternalReturnRequestData.getCharacterInfo(match.characterNum.toString())?.name ?: "未知:("
            weaponUrl = getWeaponImgUrl(match.bestWeapon)
        }
        val parseData = FreeMarkerUtils.parseData("eternal_return_war_record.ftlh", matcherData)


        return FreeMarkerUtils.parseData(
            "eternal_return_war_record_small.ftlh",
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

    private fun getCharacterImgUrl(type: EternalReturnCharacterById.UrlType, id: Int, skin: Long = -1) = run {
        imageService.getEternalReturnCharacterImage(type, id, skin)
        "http://localhost:$port/images/eternal_return/character/$type/$id/$skin"
    }

    private fun getItemImgUrl(id: Long) = run {
        imageService.getEternalReturnItemImage(id)
        "http://localhost:$port/images/eternal_return/item/${id}"
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
        "http://localhost:$/images/eternal_return/weapon/${id}"
    }


    override fun commandName(): String {
        return "eternalReturnFindPlayers"
    }

    override fun state(id: Long): Boolean {
        return true
    }
}
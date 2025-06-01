package cn.luorenmu.action.request

import action.commandProcess.eternalReturn.entity.*
import action.commandProcess.eternalReturn.entity.profile.EternalReturnProfile
import action.commandProcess.eternalReturn.entity.tier.EternalReturnTierDistributions
import cn.luorenmu.action.commandProcess.eternalReturn.entity.item.EternalReturnItemInfos
import cn.luorenmu.action.commandProcess.eternalReturn.entity.matcher.EternalReturnMatches
import cn.luorenmu.action.commandProcess.eternalReturn.entity.skill.EternalReturnTacticalSkill
import cn.luorenmu.action.commandProcess.eternalReturn.entity.skill.EternalReturnTraitSkills
import cn.luorenmu.action.commandProcess.eternalReturn.entity.tier.EternalReturnTiers
import cn.luorenmu.action.commandProcess.eternalReturn.entity.weapon.EternalReturnWeapons
import cn.luorenmu.action.request.entiy.EternalReturnTraitSkillImgDTO
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.exception.LoMuBotException
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.to
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.io.File
import java.net.SocketException
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.08.03 9:11
 */
@Component
class EternalReturnRequestData(
    private val redisUtils: RedisUtils,
    private val requestData: RequestData,
) {
    private val log = KotlinLogging.logger {}

    // sync player
    fun syncPlayers(nickname: String, counter: Int = 0): Boolean {
        if (counter == 5) {
            return true
        }
        val requestController = RequestController("eternal_return_request.find_player")
        requestController.replaceUrl("nickname", nickname)
        try {
            val request = requestController.request()
            request?.let {
                val body = request.body()
                if (body.contains("retry_after")) {
                    return syncPlayers(nickname, counter + 1)
                }
                if (body.contains("invalid name")) {
                    return false
                }
                return !body.contains("not_found")
            }
        } catch (e: SocketException) {
            return true
        } catch (e: Exception) {
            return syncPlayers(nickname, counter + 1)
        }
        return true
    }

    /**
     * 段位总览(有哪些段位?)
     */
    fun tiers(): EternalReturnTiers? {
        return redisUtils.getCache("Eternal_Return_Tiers", EternalReturnTiers::class.java, {
            val resp = requestData.requestRetry(RequestController("eternal_return_request.tiers"))
            resp?.body().to<EternalReturnTiers>()
        }, 2L, TimeUnit.DAYS)
    }

    /**
     * 匹配
     */
    fun matches(
        nickname: String,
        season: String,
        matchingMode: String = "ALL",
        teamMode: String = "ALL",
        page: Int = 1,
    ): EternalReturnMatches? {
        val requestController = RequestController("eternal_return_request.matches")
        requestController.replaceUrl("nickname", nickname)
        requestController.replaceUrl("season", season)
        requestController.replaceUrl("matching_mode", matchingMode)
        requestController.replaceUrl("team_mode", teamMode)
        requestController.replaceUrl("page", page.toString())
        val resp = requestData.requestRetry(requestController)
        return resp?.body().to<EternalReturnMatches>()
    }

    /**
     * 段位分布
     */
    fun tierDistributionsFind(): EternalReturnTierDistributions? {
        val resp = requestData.requestRetry(RequestController("eternal_return_request.tier_distribution"))
        return resp?.body().to<EternalReturnTierDistributions>()

    }

    fun leaderboardFind(): EternalReturnLeaderboard? {
        return currentSeason()?.let {
            val requestLeaderboard = RequestController("eternal_return_request.leaderboard")
            requestLeaderboard.replaceUrl("season", it.currentSeason.key)
            val respLeaderboard = requestLeaderboard.request()
            respLeaderboard?.let { resp ->
                val leaderboard = resp.body().to<EternalReturnLeaderboard>()
                leaderboard.currentSeason = it
                leaderboard
            }
        }
    }

    suspend fun dakGGDownloadStreamFile(streamUrl: String, outputPath: String) {
        val requestDetailed = RequestDetailed()
        requestDetailed.url = "https://cdn.dak.gg${streamUrl}"
        requestDetailed.method = "get"
        val request = RequestController(requestDetailed)
        val resp = request.request()
        resp?.let { ReadWriteFile.writeStreamFile(outputPath, resp.bodyStream()) }
    }

    /**
     * 获取段位图标 round
     * iconUrl
     */
    suspend fun checkTierIconExistThenGetPathOrDownload(id: Int): String {
        val eternalReturnDataImagePath = PathUtils.getEternalReturnDataImagePath("tier/${id}.png")
        if (!File(eternalReturnDataImagePath).exists()) {
            dakGGDownloadStreamFile("/er/images/tier/round/$id.png", eternalReturnDataImagePath)
        }
        return eternalReturnDataImagePath
    }


    /**
     * 英雄详细信息
     */
    fun characterDetailsFind(character: String, weapon: String, token: String): EternalReturnCharacterInfo? {
        return redisUtils.getCache("Eternal_Return_Find:${character}", EternalReturnCharacterInfo::class.java, {
            val request = RequestController("eternal_return_request.find_character_info")
            request.replaceUrl("token", token)
            request.replaceUrl("key", character)
            request.replaceUrl("key1", character)
            request.replaceUrl("weapon", weapon)
            val resp = requestData.requestRetry(request)
            resp!!.body().to<EternalReturnCharacterInfo>()
        }, 2L, TimeUnit.DAYS, EternalReturnCharacterInfo::class.java)
    }


    fun characterFind(): EternalReturnCharacter? {
        return redisUtils.getCache("Eternal_Return_Find: characters", EternalReturnCharacter::class.java, {
            val requestController = RequestController("eternal_return_request.character")
            val resp = requestData.requestRetry(requestController)
            resp!!.body().to<EternalReturnCharacter>()
        }, 2L, TimeUnit.DAYS)
    }

    fun currentSeason(): EternalReturnCurrentSeason? {
        return redisUtils.getCache("Eternal_Return_Season", EternalReturnCurrentSeason::class.java, {
            val requestCurrentSeason = RequestController("eternal_return_request.current_season")
            val respCurrentSeason = requestData.requestRetry(requestCurrentSeason)
            respCurrentSeason?.body().to<EternalReturnCurrentSeason>()
        }, 1L, TimeUnit.DAYS)
    }

    fun checkPlayerExists(name: String): Boolean {
        val requestProfile = RequestController("eternal_return_request.profile")
        requestProfile.replaceUrl("season", "SEASON_1")
        requestProfile.replaceUrl("name", name)
        val requestRetry = requestData.requestRetry(requestProfile)
        requestRetry?.let {
            return true
        }
        return false
    }

    fun profile(name: String, season: String = "SEASON_16"): EternalReturnProfile? {
        val requestProfile = RequestController("eternal_return_request.profile")
        requestProfile.replaceUrl("season", season)
        requestProfile.replaceUrl("name", name)
        val resp = requestData.requestRetry(requestProfile)
        return try {
            resp?.body().to<EternalReturnProfile>()
        } catch (e: JSONException) {
            log.error { e.printStackTrace() }
            null
        }
    }

    /**
     * 永恒轮回官网新聞
     */
    fun news(id: String): String? {
        val requestProfile = RequestController("eternal_return_request.news")
        requestProfile.replaceUrl("id", id)
        val resp = requestProfile.request()
        if (resp.status != 200) {
            return null
        }
        return resp?.body()
    }

    /**
     * 装备背景图片 表示装备品级
     * 持久化存储 应当缓存图片
     * @param id 需传递图片 数字id 00-06
     * @return 磁盘存储路径
     */
    suspend fun getItemGradeBg(id: Int): String {
        val eternalReturnDataImagePath = PathUtils.getEternalReturnDataImagePath("ico/itemgradebg-0${id}.svg")
        if (!File(eternalReturnDataImagePath).exists()) {
            // 写死 没关系 ^ ^
            dakGGDownloadStreamFile("/er/images/item/ico-itemgradebg-0${id}.svg", eternalReturnDataImagePath)
        }
        return eternalReturnDataImagePath
    }


    /**
     * 天赋图片
     * 持久化存储 应当缓存图片
     * @param id 需传递图片数字id
     * @return 磁盘存储路径
     */
    suspend fun getTraitSkillsIcon(id: Long): EternalReturnTraitSkillImgDTO {
        val skillPath = PathUtils.getEternalReturnDataImagePath("ico/TraitSkillsIcon/${id}.png")
        val traitSkills = getTraitSkills()
        traitSkills!!.let { skills ->
            val skill = skills.traitSkills.first { it.id == id }
            val skillGroup = skills.traitSkillGroups.first { skill.group == it.key }
            val skillGroupPath = PathUtils.getEternalReturnDataImagePath("ico/TraitSkillsIcon/${skillGroup.key}.png")
            if (!File(skillGroupPath).exists() || !File(skillPath).exists()) {
                downloadDakGGCompleteUrlStream(skill.imageUrl, skillPath)
                downloadDakGGCompleteUrlStream(skillGroup.imageUrl, skillGroupPath)
            }
            return EternalReturnTraitSkillImgDTO(skill = skillPath, skillGroup = skillGroupPath)
        }
    }


    /**
     *  下载文件(需要完整的uri)
     *  @param "//cdn.dak.gg/assets/er/game-assets/1.44.0/VSkillIcon_4103000.png"
     *
     */
    suspend fun downloadDakGGCompleteUrlStream(url: String, outputPath: String) {
        val resp = requestData.requestRetry({
            it.url = "https:${url}"
            it.method = "get"
        })
        ReadWriteFile.writeStreamFile(outputPath, resp?.bodyStream())
    }

    /**
     * 当前赛季的天赋
     */
    fun getTraitSkills(): EternalReturnTraitSkills? {
        return redisUtils.getCache("Eternal_Return_Trait_Skills", EternalReturnTraitSkills::class.java, {
            val requestController = RequestController(
                RequestDetailed().apply {
                    url = "https://er.dakgg.io/api/v1/data/trait-skills?hl=zh-cn"
                    method = "GET"
                }
            )
            val resp = requestData.requestRetry(requestController)
            resp?.body().to<EternalReturnTraitSkills>()
        }, 1L, TimeUnit.DAYS)
    }

    /**
     * 实验体技能(召唤师技能)
     * 闪灵、赤色风暴、激光陀螺
     */
    fun getTacticalSkills(): EternalReturnTacticalSkill? {
        return redisUtils.getCache("Eternal_Return_Tactical_Skills", EternalReturnTacticalSkill::class.java, {
            val requestController = RequestController(
                RequestDetailed().apply {
                    url = "https://er.dakgg.io/api/v1/data/tactical-skills?hl=zh-cn"
                    method = "GET"
                }
            )
            val resp = requestData.requestRetry(requestController)
            resp?.body().to<EternalReturnTacticalSkill>()
        }, 1L, TimeUnit.DAYS)
    }

    /**
     *  实验体技能(召唤师技能)图标
     */
    suspend fun getTacticalSkillIcon(id: Long): String {
        val skillPath = PathUtils.getEternalReturnDataImagePath("ico/TacticalSkillIcon/${id}.png")
        if (!File(skillPath).exists()) {
            getTacticalSkills()?.let { skill ->
                skill.tacticalSkills.first { it.id == id }.let { idSkill ->
                    downloadDakGGCompleteUrlStream(idSkill.imageUrl, skillPath)
                }
            }
        }
        return skillPath
    }


    /**
     * 物品信息 包括英雄装备、武器
     */
    fun getItems(): EternalReturnItemInfos? {
        return redisUtils.getCache("Eternal_Return_Items", EternalReturnItemInfos::class.java, {
            val requestController = RequestController(
                RequestDetailed().apply {
                    url = "https://er.dakgg.io/api/v1/data/items?hl=zh-cn"
                    method = "GET"
                }
            )
            val resp = requestData.requestRetry(requestController)
            resp?.body().to<EternalReturnItemInfos>()
        })
    }

    /**
     * 装备图片
     * 持久化存储 应当缓存图片
     * @param id 需传递图片 数字id 204419
     * @return 磁盘存储路径
     */
    suspend fun getItemIcon(id: Long): String {
        val eternalReturnDataImagePath = PathUtils.getEternalReturnDataImagePath("ico/ItemIcon/${id}.png")
        if (!File(eternalReturnDataImagePath).exists()) {
            getItems()?.let { itemInfos ->
                downloadDakGGCompleteUrlStream(
                    itemInfos.items.first { it.id == id }.imageUrl,
                    eternalReturnDataImagePath
                )
            }
        }
        return eternalReturnDataImagePath
    }


    /**
     * @param id  英雄id
     * @param characterImgUrlType 用于确定url类型以存储图片位置
     * @param skin 皮肤id
     * 持久化头像
     * 英雄图片
     */
    suspend fun getCharacterImg(
        id: Int,
        characterImgUrlType: EternalReturnCharacterById.CharacterImgUrlType,
        skin: Long = -1,
    ): String {
        val versionRegex = "(\\d+\\.\\d+\\.\\d+)".toRegex()
        val characterInfo = getCharacterInfo(id.toString())
        val eternalReturnDataImagePath =
            PathUtils.getEternalReturnDataImagePath("ico/${characterImgUrlType.type}/${id}.png")
        if (!File(eternalReturnDataImagePath).exists()) {
            characterInfo.let {
                val url: String = if (skin != -1L) {
                    val skinInfo = it.skins.first { skinObj -> skinObj.id == skin }
                    when (characterImgUrlType) {
                        EternalReturnCharacterById.CharacterImgUrlType.ImageUrl ->
                            skinInfo.imageUrl

                        EternalReturnCharacterById.CharacterImgUrlType.FullImageUrl ->
                            skinInfo.fullImageUrl

                        EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl ->
                            "//cdn.dak.gg/assets/er/game-assets/${versionRegex.find(skinInfo.imageUrl)!!.value}/CharProfile_${skinInfo.imageName}.png"

                        else ->
                            skinInfo.imageUrl
                    }
                } else {
                    when (characterImgUrlType) {
                        EternalReturnCharacterById.CharacterImgUrlType.BackgroundImageUrl ->
                            it.backgroundImageUrl

                        EternalReturnCharacterById.CharacterImgUrlType.FullImageUrl ->
                            it.fullImageUrl

                        EternalReturnCharacterById.CharacterImgUrlType.ResultImageUrl ->
                            it.resultImageUrl

                        EternalReturnCharacterById.CharacterImgUrlType.CommunityImageUrl ->
                            it.communityImageUrl

                        EternalReturnCharacterById.CharacterImgUrlType.CharProfileImageUrl ->
                            "//cdn.dak.gg/assets/er/game-assets/${versionRegex.find(it.imageUrl)!!.value}/CharProfile_${it.imageName}.png"

                        else -> it.resultImageUrl
                    }
                }

                downloadDakGGCompleteUrlStream(url, eternalReturnDataImagePath)
            }
        }
        return eternalReturnDataImagePath
    }

    /**
     * 所有武器信息
     */
    fun getWeapons(): EternalReturnWeapons? {
        return redisUtils.getCache("Eternal_Return_Weapons", EternalReturnWeapons::class.java, {
            val resp = requestData.requestRetry(RequestController(RequestDetailed().apply {
                url = "https://er.dakgg.io/api/v1/data/masteries?hl=zh_CN"
                method = "get"
            }))
            resp?.body().to<EternalReturnWeapons>()
        }, 2L, TimeUnit.DAYS)
    }

    /**
     * 武器图片
     * @param id
     * @return 存儲路径
     */
    suspend fun getWeaponIcon(id: Int): String {
        val eternalReturnDataImagePath =
            PathUtils.getEternalReturnDataImagePath("ico/WeaponsIcon/${id}.png")
        if (!File(eternalReturnDataImagePath).exists()) {
            getWeapons()?.let { weapons ->
                val iconUrl = weapons.masteries.first { it.id == id }.iconUrl
                downloadDakGGCompleteUrlStream(iconUrl, eternalReturnDataImagePath)
            }
        }
        return eternalReturnDataImagePath
    }

    /**
     *  具体英雄信息 通过id、key、name获取
     *  无法获取到具体的英雄
     */
    suspend fun getCharacterInfo(id: String, retry: Boolean = true): EternalReturnCharacterById {
        characterFind()?.let { character ->
            if (StringUtils.isNumeric(id)) {
                return character.characters.first { it.id == id.toInt() }
            }
            return character.characters.firstOrNull { it.key == id } ?: run {
                character.characters.first { it.name == id }
            }
        }
        if (retry) {
            redisUtils.deleteCache("Eternal_Return_Find: characters")
            getCharacterInfo(id, false)
        }
        throw LoMuBotException("获取英雄信息失败")
    }

}
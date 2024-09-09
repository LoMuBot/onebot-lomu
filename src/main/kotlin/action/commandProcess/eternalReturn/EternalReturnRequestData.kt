package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandHandle.entiy.eternalReturn.*
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalRetrunLeaderboard
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.profile.EternalReturnProfile
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnCharacterInfo
import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnCurrentSeason
import cn.luorenmu.common.utils.dakggCdnUrl
import cn.luorenmu.common.utils.getEternalReturnDataImagePath
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import org.springframework.data.redis.core.RedisTemplate
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
    private val redisTemplate: RedisTemplate<String, String>,
) {

    // sync player
    fun findExistPlayers(nickname: String): Boolean {
        val requestController = RequestController("eternal_return_request.find_player")
        requestController.replaceUrl("nickname", nickname)
        try {
            var request = requestController.request()
            var body = request.body()
            if (body.contains("retry_after")) {
                request = requestController.request()
                body = request.body()
            }
            if(body.contains("invalid name")){
                return false
            }
            return !body.contains("not_found")
        } catch (e: SocketException) {
            return true
        }
    }



    fun tierDistributionsFind(): EternalReturnTierDistributions? {
        val request = RequestController("eternal_return_request.tier_distribution")
        val resp = request.request()
        return resp?.body().to<EternalReturnTierDistributions>()

    }

    fun leaderboardFind(): EternalRetrunLeaderboard? {
        return currentSeason()?.let {
            val requestLeaderboard = RequestController("eternal_return_request.leaderboard")
            requestLeaderboard.replaceUrl("season", it.currentSeason.key)
            val respLeaderboard = requestLeaderboard.request()
            respLeaderboard?.let { respLeaderboard ->
                val leaderboard = respLeaderboard.body().to<EternalRetrunLeaderboard>()
                leaderboard.currentSeason = it
                leaderboard
            }
        }
    }

    fun checkCharacterImgExistThenGetPathOrDownload(name: String): String {
        val eternalReturnDataImagePath = getEternalReturnDataImagePath("character/${name}.png")
        val fileExists = File(eternalReturnDataImagePath).exists()
        if (!fileExists) {
            characterFind().let {
                val character = it?.characters?.stream()?.filter { c -> c.key == name }?.findFirst()
                val communityImageUrl = character!!.get().communityImageUrl
                val dakGGCdnUrl = dakggCdnUrl(communityImageUrl)
                val requestDetailed = RequestDetailed()
                requestDetailed.url = dakGGCdnUrl
                requestDetailed.method = "get"
                val request = RequestController(requestDetailed)
                val resp = request.request()
                resp?.let { ReadWriteFile.writeStreamFile(eternalReturnDataImagePath, resp.bodyStream()) }
            }
        }
        return getEternalReturnDataImagePath("character/${name}.png")
    }

    fun characterLeaderboardFind(character: String, sortType: String): EternalReturnLeaderboardCharacters? {
        return currentSeason()?.let {
            val leaderboardCharacters = RequestController("eternal_return_request.leaderboard_characters")
            leaderboardCharacters.replaceUrl("season", it.currentSeason.key)
            leaderboardCharacters.replaceUrl("character", character)
            leaderboardCharacters.replaceUrl("sortType", sortType)
            val resp = leaderboardCharacters.request()
            resp?.body().to<EternalReturnLeaderboardCharacters>()
        }

    }

    fun characterInfoFind(character: String): EternalReturnCharacterInfo? {
        redisTemplate.opsForValue().get("Eternal_Return_Find:${character}")?.to<EternalReturnCharacterInfo>()
        val request = RequestController("eternal_return_request.find_character_info")
        request.replaceUrl("key", character)
        request.replaceUrl("key1", character)
        val resp = request.request()
        return resp?.let {
            val result = it.body().to<EternalReturnCharacterInfo>()
            redisTemplate.opsForValue()["Eternal_Return_Find:${character}", it.body(), 1L] =
                TimeUnit.DAYS
            result
        }

    }

    fun characterFind(): EternalReturnCharacter? {
        redisTemplate.opsForValue().get("Eternal_Return_Find: characters")?.let {
            return it.to<EternalReturnCharacter>()
        }
        val requestController = RequestController("eternal_return_request.character")
        val resp = requestController.request()
        return resp?.let {
            val characters = it.body().to<EternalReturnCharacter>()
            redisTemplate.opsForValue()["Eternal_Return_Find: characters", it.body(), 1L] =
                TimeUnit.DAYS
            characters
        }

    }

    fun currentSeason(): EternalReturnCurrentSeason? {
        val requestCurrentSeason = RequestController("eternal_return_request.current_season")
        val respCurrentSeason = requestCurrentSeason.request()
        return respCurrentSeason?.body().to<EternalReturnCurrentSeason>()
    }

    fun profile(season: String): EternalReturnProfile? {
        val requestProfile = RequestController("eternal_return_request.profile")
        requestProfile.replaceUrl("season", season)
        val resp = requestProfile.request()
        return resp?.body().to<EternalReturnProfile>()
    }
}
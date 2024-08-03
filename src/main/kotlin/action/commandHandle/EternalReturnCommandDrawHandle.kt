package cn.luorenmu.action.commandHandle

import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.dakggCdnUrl
import cn.luorenmu.common.utils.getEternalReturnImagePath
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.31 0:09
 */
@Component
class EternalReturnCommandDrawHandle(
    private val redisTemplate: RedisTemplate<String, String>,
    private val eternalReturnDraw: EternalReturnDraw,
    private val eternalReturnRequestData: EternalReturnRequestData,
) {


    fun eternalReturnFindPlayers(nickname: String, id: Int): String {
        // check name
        if (nickname.isBlank() || nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        val opsForValue = redisTemplate.opsForValue()

        // check cache
        val nicknameData = opsForValue["Eternal_Return_NickName:$nickname"]
        if (nicknameData != null) {
            return nicknameData
        }

        if (!eternalReturnRequestData.findExistPlayers(nickname)) {
            val notFound = MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
            opsForValue["Eternal_Return_NickName:$nickname", notFound, 7L] = TimeUnit.DAYS
            return notFound
        }

        // TODO:
        return ""
    }


    private fun doubleToPercentage(value: Double): String {
        return "%.1f%%".format(value * 100)
    }

    // 排名
    fun leaderboard(i: Int): String {
        val top = i - 1
        // check cache
        val value = redisTemplate.opsForValue()["Eternal_Return_NickName_Top:${i}"]
        if (value != null) {
            return value
        }
        var topOnePlayerInfo = ""
        eternalReturnRequestData.leaderboardFind()?.let {
            eternalReturnRequestData.characterFind()?.let { characters ->
                val eternalReturnLeaderboardPlayer = it.leaderboards[top]
                val mostCharacters = eternalReturnLeaderboardPlayer.mostCharacters

                var tierType = 6
                var tierUrl = "/er/images/tier/round/6.png"
                for (cutoff in it.cutoffs) {
                    if (eternalReturnLeaderboardPlayer.mmr >= cutoff.mmr) {
                        tierType = cutoff.tierType
                    }
                }

                for (tierDistributionDto in it.tierDistributionDtos) {
                    if (tierDistributionDto.tierType == tierType) {
                        tierUrl = tierDistributionDto.tierImageUrl
                    }
                }

                topOnePlayerInfo = """
                ${MsgUtils.builder().img(dakggCdnUrl(tierUrl)).build()}
            """.trimIndent()


                for (mostCharacter in mostCharacters) {
                    topOnePlayerInfo += """
                    ${
                        MsgUtils.builder()
                            .img(dakggCdnUrl(characters.characters[mostCharacter.characterId].communityImageUrl))
                            .build()
                    }
                """.trimIndent()
                }

                topOnePlayerInfo += """
                Asia排行榜      $i
                游戏昵称        ${eternalReturnLeaderboardPlayer.nickname}
                游戏场次        ${eternalReturnLeaderboardPlayer.playCount}
                场均击杀        ${eternalReturnLeaderboardPlayer.avgPlayerKill.toInt()}
                mmr            ${eternalReturnLeaderboardPlayer.mmr}
                选择率          -
            """.trimIndent()

                for (mostCharacter in mostCharacters) {
                    topOnePlayerInfo += "${doubleToPercentage(mostCharacter.pickRate)}-"
                }
                topOnePlayerInfo += "\n查询战绩${eternalReturnLeaderboardPlayer.nickname}"

                redisTemplate.opsForValue()["Eternal_Return_NickName_Top:${i}", topOnePlayerInfo, 12L] = TimeUnit.HOURS
            }
        }
        return topOnePlayerInfo
    }


    fun characterLeaderborad(characterChineseName: String, sortType: String) {
        eternalReturnRequestData.characterFind()?.let {
            var characterKey = "Jackie"
            for (character in it.characters) {
                if (character.name == characterChineseName) {
                    characterKey = character.key
                }
            }
            eternalReturnRequestData.characterLeaderboardFind(characterKey, sortType)?.characterById

        }
    }

    //分数限
    fun cutoffs(): String {
        return eternalReturnDraw.cutoffs()
    }


}
package cn.luorenmu.action.commandHandle

import cn.luorenmu.common.utils.firstPinYin
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.31 0:09
 */
@Component
class EternalReturnCommandHandle(
    private val redisTemplate: RedisTemplate<String, String>,
    private val eternalReturnDraw: EternalReturnDraw,
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
) {
    /**
     * 实验体绰号名修正
     * 字符修正
     */
    private fun correctName(name: String): String {
        return name.replace(Regex("amp;"), "")
    }

    fun eternalReturnFindCharacter(characterTemp: String, i: Int): String {
        val character = correctName(characterTemp)
        val characterFind = eternalReturnRequestData.characterFind()
        var characterKey = ""
        var i1 = i
        if (i <= 0) {
            i1 = 1
        }
        redisTemplate.opsForValue()["Eternal_Return_character:${character}-${i}"]?.let {
            return it
        }
        characterFind?.let {
            val correctCharacter = firstPinYin(character)
            for (character1 in it.characters) {
                if (firstPinYin(character1.name) == correctCharacter) {
                    characterKey = character1.key
                    break
                }
                if (character1.key.lowercase() == character.lowercase()) {
                    characterKey = character1.key
                    break
                }
            }

            if (characterKey.isBlank()) {
                return ""
            }


            var rapier = ""
            var rapierList: List<String> = listOf()
            val characterInfo = eternalReturnRequestData.characterInfoFind(characterKey)
            characterInfo?.let { cI ->
                if (i1 > cI.pageProps.randomCharacter.masteries.size) {
                    i1 = 1
                }
                rapierList = cI.pageProps.randomCharacter.masteries
                rapier = cI.pageProps.randomCharacter.masteries[i1 - 1]
            }

            val rapierStr = StringBuilder()
            rapierStr.append("武器选择")
            for (index in rapierList.indices) {
                rapierStr.append("  ${index + 1}:${rapierList[index]}  ")
            }

            return eternalReturnWebPageScreenshot.webCharacterScreenshot(characterKey, rapier, i) + rapierStr.toString()

        }

        return ""
    }

    fun eternalReturnFindPlayers(nickname: String): String {
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

        return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)
    }


    // 排名
    fun leaderboard(i: Int): String {
        return eternalReturnDraw.leaderboard(i)
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
package cn.luorenmu.action.commandHandle

import cn.luorenmu.common.utils.dakggCdnUrl
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
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
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

        return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)
    }


    private fun doubleToPercentage(value: Double): String {
        return "%.1f%%".format(value * 100)
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
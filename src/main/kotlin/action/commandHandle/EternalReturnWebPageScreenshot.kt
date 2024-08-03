package cn.luorenmu.action.commandHandle

import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.getEternalReturnImagePath
import cn.luorenmu.common.utils.getEternalReturnNicknameImagePath
import cn.luorenmu.web.WebPageScreenshot
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.imageio.IIOException

/**
 * @author LoMu
 * Date 2024.08.03 9:17
 */
@Component
class EternalReturnWebPageScreenshot(
    private val webPageScreenshot: WebPageScreenshot,
    private val redisTemplate: RedisTemplate<String, String>,

    ) {


    //分数限
    fun cutoffs(): String {
        val opsForValue = redisTemplate.opsForValue()
        val path = getEternalReturnImagePath("leaderboard_cutoffs")
        val url = JsonObjectUtils.getString("request.eternal_return_request.leaderboard_cutoffs")
        val cq = MsgUtils.builder().img(path).build()
        synchronized(webPageScreenshot) {
            // check cache
            val data = opsForValue["Eternal_Return:cutoffs"]
            if (data != null) {
                return data
            }
            webPageScreenshot.setHttpUrl(url).screenshotCrop(414, 581, 1074, 144)
                .outputImageFile(path)
            opsForValue["Eternal_Return:cutoffs", cq, 12L] = TimeUnit.HOURS
        }
        return cq
    }

    fun webPlayerPageScreenshot(nickname: String): String {
        val opsForValue = redisTemplate.opsForValue()
        var url = JsonObjectUtils.getString("request.eternal_return_request.players")
        url = MatcherData.replaceDollardName(url, "nickname", nickname)
        val path = getEternalReturnNicknameImagePath(nickname)
        val returnMsg =
            MsgUtils.builder().img(OneBotMedia().file(path).cache(false).proxy(false)).build()
        synchronized(webPageScreenshot) {
            try {
                // redis check
                val safeCheckData = opsForValue["Eternal_Return_NickName:$nickname"]
                safeCheckData?.let {
                    return it
                }
                webPageScreenshot.setHttpUrl(url).screenshotAllCrop(381, 150, 1131, -500, 3000).outputImageFile(path)
                opsForValue["Eternal_Return_NickName:$nickname", returnMsg, 20L] = TimeUnit.MINUTES
                return returnMsg
            } catch (e: IIOException) {
                return MsgUtils.builder().text("名称不合法 $nickname").build()
            }
        }
    }
}
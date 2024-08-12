package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.getEternalReturnImagePath
import cn.luorenmu.common.utils.getEternalReturnNicknameImagePath
import cn.luorenmu.pool.WebPageScreenshotPool
import cn.luorenmu.web.WebPageScreenshot
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.imageio.IIOException

/**
 * @author LoMu
 * Date 2024.08.03 9:17
 */
@Component
class EternalReturnWebPageScreenshot(
    private val webPageScreenshot: WebPageScreenshotPool,
    private val redisTemplate: RedisTemplate<String, String>,
    private val nickNameMap: MutableMap<String, Future<*>> = mutableMapOf(),

    ) {


    //分数限
    fun cutoffs(): String {
        val opsForValue = redisTemplate.opsForValue()
        val path = getEternalReturnImagePath("leaderboard_cutoffs")
        val url = JsonObjectUtils.getString("request.eternal_return_request.leaderboard_cutoffs")
        val cq = MsgUtils.builder().img(path).build()

        synchronized(webPageScreenshot) {
            // check cache
            cacheCheck("Eternal_Return:cutoffs")?.let { return it }

        }
        webPageScreenshot.execute {
            it.setHttpUrl(url).screenshotCrop(414, 581, 1074, 144).outputImageFile(path)
            opsForValue["Eternal_Return:cutoffs", cq, 12L] = TimeUnit.HOURS
        }

        cacheCheck("Eternal_Return:cutoffs")?.let { return it }
        return cq
    }

    fun webCharacterScreenshot(character: String, rapier: String, cacheIndex: Int): String {
        val cacheName = "Eternal_Return_character:${character}-${cacheIndex}"
        //再次检查 以防万一
        cacheCheck(cacheName)?.let { return it }
        var url = JsonObjectUtils.getString("request.eternal_return_request.find_character")
        url = MatcherData.replaceDollardName(url, "characterName", character)
        url = MatcherData.replaceDollardName(url, "rapier", rapier)
        val path = getEternalReturnImagePath("character/${character}-${rapier}.png")
        val msgCQ = MsgUtils.builder().img(path).build()
        val f = syncWebPageScreenshot(cacheName, msgCQ, 1L, TimeUnit.DAYS) {
            it.setHttpUrl(url)
                .screenshotAllCrop(381, 700, 1131, -1500, 2000).outputImageFile(path)
        }
        f?.get() ?: run {
            nickNameMap.remove(cacheName)
        }
        return msgCQ
    }

    /**
     * 玩家战绩页面截图
     */
    fun webPlayerPageScreenshot(nickname: String): String {
        val cacheName = "Eternal_Return_NickName:$nickname"

        var url = JsonObjectUtils.getString("request.eternal_return_request.players")
        url = MatcherData.replaceDollardName(url, "nickname", nickname)
        val path = getEternalReturnNicknameImagePath(nickname)
        val returnMsg = MsgUtils.builder().img(OneBotMedia().file(path).cache(false).proxy(false)).build()



        try {
            syncWebPageScreenshot(cacheName, returnMsg, 20L, TimeUnit.MINUTES) {
                it.setHttpUrl(url).screenshotAllCrop(381, 150, 1131, -500, 3000).outputImageFile(path)
            }?.get() ?: run {
                nickNameMap.remove(cacheName)
            }
        } catch (e: IIOException) {
            return MsgUtils.builder().text("名称不合法 $nickname").build()
        }
        return returnMsg
    }


    /**
     *  检查缓存 如果存在则返回Null 不存在则检查队列 如果存在等待任务则返回 调用该任务后需同步等待截图
     */
    @Synchronized
    fun syncWebPageScreenshot(
        cacheName: String,
        cacheMsg: String,
        time: Long,
        timeUnit: TimeUnit,
        web: (WebPageScreenshot) -> Unit,
    ): Future<*>? {
        val opsForValue = redisTemplate.opsForValue()
        // redis check
        cacheCheck(cacheName)?.let { return null }
        //如果任务正在处理 等待任务完成 不存在则开始截图
        nickNameMap[cacheName]?.let { return it } ?: run {
            nickNameMap[cacheName] = webPageScreenshot.execute {
                web(it)
                opsForValue[cacheName, cacheMsg, time] = timeUnit
            }
            return nickNameMap[cacheName]!!
        }
    }


    // redis check
    fun cacheCheck(name: String): String? {
        val opsForValue = redisTemplate.opsForValue()
        return opsForValue[name]
    }
}
package cn.luorenmu.action.webPageScreenshot

import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.pool.WebPageScreenshotPool
import cn.luorenmu.web.WebPageScreenshot
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author LoMu
 * Date 2024.08.03 9:17
 */
@Component
class EternalReturnWebPageScreenshot(
    private val webPageScreenshot: WebPageScreenshotPool,
    private val redisTemplate: RedisTemplate<String, String>,
    private val nickNameMap: MutableMap<String, Future<*>> = ConcurrentHashMap(),
) {

    private val log = KotlinLogging.logger { }

    // 角色页面
    fun webCharacterScreenshot(character: String, weapon: String, cacheMsg: String): String {
        val cacheName = "Eternal_Return_character:${character}-${weapon}"
        cacheCheck(cacheName)?.let { return it }
        var url = JsonObjectUtils.getString("request.eternal_return_request.find_character")
        url = MatcherData.replaceDollardName(url, "characterName", character)
        url = MatcherData.replaceDollardName(url, "weapon", weapon)
        val path = PathUtils.getEternalReturnImagePath("character/${character}-${weapon}.png")
        val msgCQ = MsgUtils.builder().img(path).text(cacheMsg).build()
        val f = syncWebPageScreenshot(cacheName, msgCQ, 1L, TimeUnit.DAYS) {
            it.setHttpUrl(url)
                .screenshotAllCrop(660, 235, 835, -500, 50) {
                    TimeUnit.SECONDS.sleep(2)
                }.outputImageFile(path)
        }
        f?.get() ?: run {
            nickNameMap.remove(cacheName)
        }
        log.info { "已完成的截图: $cacheName" }
        return msgCQ
    }

    /**
     * 玩家战绩页面截图
     * TODO 非常的不可靠 待重写 手动渲染为图片
     */
    fun webPlayerPageScreenshot(nickname: String): String {
        val cacheName = "Eternal_Return_NickName:$nickname"

        var url = JsonObjectUtils.getString("request.eternal_return_request.players")
        url = MatcherData.replaceDollardName(url, "nickname", nickname)
        val path = PathUtils.getEternalReturnNicknameImagePath(nickname)
        val returnMsg = MsgUtils.builder().img(OneBotMedia().file(path).cache(false).proxy(false)).build()
        try {
            syncWebPageScreenshot(cacheName, returnMsg, 20L, TimeUnit.MINUTES) {
                it.setHttpUrl(url).screenshotAllCrop(381, 200, 1131, -600, 50) {
                    TimeUnit.SECONDS.sleep(4)
                }.outputImageFile(path)
            }?.get(2, TimeUnit.MINUTES) ?: run {
                nickNameMap.remove(cacheName)
            }
        } catch (e: TimeoutException) {
            // 首次启动selenium访问页面加载会非常慢
            return "任务执行时间超出预期已被强制取消,这可能是因为程序正在初始化或服务器网络无法正常请求"
        }
        log.info { "已完成的截图: $cacheName" }

        return returnMsg
    }


    /**
     *  检查缓存 如果存在则返回Null 不存在则检查队列 如果存在等待任务则返回 调用该任务后需同步等待截图
     *  当结果返回null时 表示该任务其他线程已经处理完成
     */
    fun syncWebPageScreenshot(
        cacheName: String,
        cacheMsg: String,
        time: Long,
        timeUnit: TimeUnit,
        web: (WebPageScreenshot) -> Unit,
    ): Future<*>? {
        log.info { "正在执行页面截图: $cacheName" }
        val opsForValue = redisTemplate.opsForValue()
        cacheCheck(cacheName)?.let {
            log.info { "缓存中已存在: $cacheName" }
            return null
        }
        //如果任务正在处理 等待任务完成 不存在则开始截图
        nickNameMap[cacheName]?.let {
            log.info { "同一任务正在处理: $cacheName" }
            return it
        } ?: run {
            nickNameMap[cacheName] = webPageScreenshot.execute {
                web(it)
                opsForValue[cacheName, cacheMsg, time] = timeUnit
            }
            return nickNameMap[cacheName]!!
        }
    }


    fun cacheCheck(name: String): String? {
        val opsForValue = redisTemplate.opsForValue()
        return opsForValue[name]
    }
}
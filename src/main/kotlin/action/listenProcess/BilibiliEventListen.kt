package cn.luorenmu.action.listenProcess

import cn.luorenmu.common.extensions.sendGroupMsgLimit
import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.getVideoPath
import cn.luorenmu.entiy.Request
import cn.luorenmu.entiy.WaitDeleteFile
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.OneBotConfig
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

/**
 * @author LoMu
 * Date 2024.09.12 21:13
 */
@Component
class BilibiliEventListen(
    val bilibiliRequestData: BilibiliRequestData,
    val oneBotConfigRepository: OneBotConfigRepository,
    val redisTemplate: StringRedisTemplate,
) {
    val bilibiliVideoLongLink = "BV[a-zA-Z0-9]+"
    val bilibiliVideoShortLink = "https://b23.tv/([a-zA-Z0-9]+)"


    fun process(bot: Bot, groupId: Long, message: String) {
        val correctMsg = message.replace("\\", "")
        findBilibiliLinkBvid(correctMsg)?.let {
            val videoPath = getVideoPath("bilibili/$it.flv")
            val videoPathCQ = MsgUtils.builder().video(videoPath, "").build()
            //检查缓存
            redisTemplate.opsForValue()["bilibili_videoInfo:$it"]?.let { info ->
                bot.sendGroupMsg(groupId, info, false)
                redisTemplate.opsForValue()["bilibili_video:$it"]?.let { video ->
                    bot.sendGroupMsg(groupId, video, false)
                }
                return
            }

            bilibiliRequestData.bvidToCid(it)?.let { videoInfo ->
                var videoInfoStr = ""
                videoInfo.firstFrame?.let {
                    videoInfoStr += MsgUtils.builder().img(it).build()
                }
                videoInfoStr += videoInfo.part
                redisTemplate.opsForValue()["bilibili_videoInfo:$it", videoInfoStr, 20L] = TimeUnit.MINUTES
                bot.sendGroupMsgLimit(
                    groupId,
                    videoInfoStr
                )

                if (downloadVideo(it, videoInfo.cid, videoPath)) {
                    redisTemplate.opsForValue()["bilibili_video:$it", videoPathCQ, 20L] = TimeUnit.MINUTES
                    oneBotConfigRepository.save(
                        OneBotConfig(
                            null,
                            "waitDeleteFile",
                            WaitDeleteFile(
                                videoPath,
                                LocalDateTime.now(),
                                LocalDateTime.now().plusMinutes(30)
                            ).toJSONString()
                        )
                    )
                    bot.sendGroupMsgLimit(groupId, videoPathCQ)
                } else {
                    bot.sendGroupMsg(groupId, "video download failed or video too large", false)
                }
            }
        }

    }


    fun findBilibiliLinkBvid(message: String): String? {

        if (message.contains(bilibiliVideoLongLink.toRegex())) {
            return MatcherData.matcherIndexStr(message, bilibiliVideoLongLink, 0).getOrNull()
        }
        if (message.contains(bilibiliVideoShortLink.toRegex())) {
            // short link to long link
            val shortLink = MatcherData.matcherStr(message, bilibiliVideoShortLink, 1, "").getOrNull()

            val respBody = RequestController(Request.RequestDetailed().apply {
                url = shortLink!!
                method = "GET"
            }).request().body()

            return MatcherData.matcherIndexStr(respBody, bilibiliVideoLongLink, 0).getOrNull()
        }

        return null
    }


    /**
     *  @param bvid (bv号)
     *  @param outputPath video save local path need file name and video type (default type is flv)
     *  @return null if video download failed or video too large (limit length $minute minute) else string
     */
    fun downloadVideo(bvid: String, cid: Long, outputPath: String): Boolean {
        val videoInfos = bilibiliRequestData.getVideoInfo(bvid, cid)
        videoInfos?.let {
            val minute = videoInfos.timelength / 1000 / 60
            if (minute > 5) {
                return false
            }
            videoInfos.durl.firstOrNull()?.let { videoInfo ->
                bilibiliRequestData.downloadVideo(videoInfo.url, outputPath)
                return true
            }
        }
        return false
    }

}
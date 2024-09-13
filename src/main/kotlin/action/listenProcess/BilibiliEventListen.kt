package cn.luorenmu.action.listenProcess

import cn.luorenmu.common.utils.MatcherData
import cn.luorenmu.common.utils.getVideoPath
import cn.luorenmu.entiy.Request
import cn.luorenmu.entiy.WaitDeleteFile
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.OneBotConfig
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

/**
 * @author LoMu
 * Date 2024.09.12 21:13
 */
@Component
class BilibiliEventListen(
    val bilibiliRequestData: BilibiliRequestData,
    val oneBotConfigRepository: OneBotConfigRepository,
) {
    val bilibiliVideoLongLink = "BV[a-zA-Z0-9]+"
    val bilibiliVideoShortLink = "https://b23.tv/([a-zA-Z0-9]+)"


    fun process(message: String): String? {
        val correctMsg = message.replace("\\", "")
        findBilibiliLinkBvid(correctMsg)?.let {
            val videoPath = getVideoPath("bilibili/$it.flv")
            if (downloadVideo(it, videoPath)) {
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
                return MsgUtils.builder().video(videoPath, "").build()
            }
        }
        return null
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
     *  @return false if video download failed or video too large (limit length 4 minute) else true
     */
    fun downloadVideo(bvid: String, outputPath: String): Boolean {
        val cid = bilibiliRequestData.bvidToCid(bvid)
        cid?.let {
            bilibiliRequestData.getVideoInfo(bvid, cid)?.let { videoInfos ->
                val minute = videoInfos.timelength / 1000 / 60
                if (minute > 4) {
                    return false
                }
                videoInfos.durl.firstOrNull()?.let { videoInfo ->
                    bilibiliRequestData.downloadVideo(videoInfo.url, outputPath)
                    return true
                }
            }
        }
        return false
    }
}
package cn.luorenmu.action.request

import cn.luorenmu.action.request.entiy.bilibili.*
import cn.luorenmu.entiy.Request
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import org.springframework.stereotype.Component
import kotlin.math.log

/**
 * @author LoMu
 * Date 2024.09.12 21:19
 */

/**
 * 0：成功
 * -400：请求错误
 * -404：无视频
 */
@Component
class BilibiliRequestData {

    fun downloadVideo(url: String, outputPath: String): Boolean {

        val requestDetailed = Request.RequestDetailed()
        val headers = Request.RequestParam("referer", "https://www.bilibili.com")
        requestDetailed.url = url
        requestDetailed.method = "GET"
        requestDetailed.headers = listOf(headers)
        val requestController = RequestController(requestDetailed)
        val resp = requestController.request()
        resp?.let {
            val stream = resp.bodyStream()
            ReadWriteFile.writeStreamFile(outputPath, stream)
            return true
        }
        return false
    }


    fun getVideoInfo(bvid: String, cid: Long): BilibiliVideoInfoStreamData? {
        val requestController = RequestController("bilibili_request.video_stream")
        requestController.replaceUrl("bvid", bvid)
        requestController.replaceUrl("cid", cid.toString())
        val resp = requestController.request()
        resp?.let {
            val body = it.body()
            val result = body.to<BilibiliVideoStreamInfo>()
            if (result.code == 0) {
                return result.data.firstOrNull()
            }
        }
        return null
    }

    fun info(bvid: String): BilibiliVideoInfoData? {
        val requestController = RequestController("bilibili_request.info")
        requestController.replaceUrl("bvid", bvid)
        val resp = requestController.request()
        resp?.let {
            try {
                val result = it.body().to<BilibiliVideoInfoResponse>()
                return result.data.firstOrNull()
            } catch (e: Exception) {
                return null
            }

        }
        return null
    }


    fun bvidToCid(bvid: String): BilibiliPageInfoData? {
        val requestController = RequestController("bilibili_request.bvid_to_cid")
        requestController.replaceUrl("bvid", bvid)
        val resp = requestController.request()
        resp?.let {
            val result = it.body().to<BilibiliPageListInfo>()
            return result.data.firstOrNull()
        }
        return null
    }
}
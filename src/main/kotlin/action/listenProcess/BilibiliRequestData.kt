package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.listenProcess.entiy.BilibiliPageInfoData
import cn.luorenmu.action.listenProcess.entiy.BilibiliPageListInfo
import cn.luorenmu.action.listenProcess.entiy.BilibiliVideoInfo
import cn.luorenmu.action.listenProcess.entiy.BilibiliVideoInfoData
import cn.luorenmu.entiy.Request
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import org.springframework.stereotype.Component

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


    fun getVideoInfo(bvid: String, cid: Long): BilibiliVideoInfoData? {
        val requestController = RequestController("bilibili_request.get_video_info")
        requestController.setWaitTime(1L)
        requestController.replaceUrl("bvid", bvid)
        requestController.replaceUrl("cid", cid.toString())
        val resp = requestController.request()
        resp?.let {
            val body = it.body()
            val result = body.to<BilibiliVideoInfo>()
            if (result.code == 0) {
                return result.data.firstOrNull()
            }
        }
        return null
    }


    fun bvidToCid(bvid: String): BilibiliPageInfoData? {
        val requestController = RequestController("bilibili_request.bvid_to_cid")
        requestController.replaceUrl("bvid", bvid)
        var resp = requestController.request()
        resp?.let {
            val result = it.body().to<BilibiliPageListInfo>()
            return result.data.firstOrNull()
        }
        return null
    }
}
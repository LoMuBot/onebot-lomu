package cn.luorenmu.action.request

import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.20 23:41
 */
@Component
class RequestData {
    fun downloadStream(url: String, path: String) {
        val requestController = RequestController(RequestDetailed().apply {
            this.url = url
            method = "GET"
        })
        val resp = requestController.request()
        ReadWriteFile.writeStreamFile(path, resp.bodyStream())
    }
}
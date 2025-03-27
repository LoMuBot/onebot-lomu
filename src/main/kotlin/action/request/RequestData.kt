package cn.luorenmu.action.request

import cn.hutool.http.HttpResponse
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

    fun requestRetry(requestController: RequestController, retry: Int = 3): HttpResponse? {
        val resp = requestController.request()
        resp?.let {
            if (resp.status == 200) {
                return resp
            } else if (resp.status == 404) {
                return null
            } else {
                if (retry > 0) {
                    return requestRetry(requestController, retry - 1)
                }
            }
        } ?: run {
            if (retry > 0) {
                return requestRetry(requestController, retry - 1)
            }
        }
        return null
    }

    fun requestRetry(requestDetailed: (RequestDetailed) -> RequestDetailed, retry: Int = 3): HttpResponse? {
        val requestController = RequestController(requestDetailed(RequestDetailed()))
        return requestRetry(requestController, retry)
    }
}
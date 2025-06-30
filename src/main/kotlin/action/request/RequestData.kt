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

    fun requestRetry(requestController: RequestController, retry: Int = 5): HttpResponse? {
        try {
            if (retry == 0){
                return null
            }
            val resp = requestController.request()
            resp?.let {
                when (resp.status) {
                    200 -> {
                        return resp
                    }
                    404 -> {
                        return null
                    }
                    else -> {
                        return requestRetry(requestController, retry - 1)
                    }
                }
            } ?: run {
                return requestRetry(requestController, retry - 1)
            }
        }catch (e: Exception){
            return requestRetry(requestController, retry - 1)
        }
    }

    fun requestRetry(requestDetailedLmd: (RequestDetailed) -> Unit, retry: Int = 3): HttpResponse? {
        val requestDetailed = RequestDetailed()
        requestDetailedLmd(requestDetailed)
        val requestController = RequestController(requestDetailed)
        return requestRetry(requestController, retry)
    }
    fun requestRetry(requestDetailedLmd: (RequestDetailed) -> Unit): HttpResponse? {
        return requestRetry(requestDetailedLmd,3)
    }
}
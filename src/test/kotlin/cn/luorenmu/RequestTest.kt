package cn.luorenmu

import cn.hutool.http.HttpResponse
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to


/**
 * @author LoMu
 * Date 2025.02.20 14:13
 */
fun main() {

    val resp = requestRetry {
        it.url =
            "https://er.dakgg.io/api/v1/players/Fwitchu/matches/31/49565010"
        it.method = "get"
    }
    println(resp?.body().to<EternalReturnMatchesById>())
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
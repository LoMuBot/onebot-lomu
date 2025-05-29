package cn.luorenmu

import cn.luorenmu.entiy.Request
import cn.luorenmu.request.RequestController

/**
 * @author LoMu
 * Date 2025.02.20 14:13
 */
fun main() {
    val request = RequestController(Request.RequestDetailed().apply {
        url = "https://dak.gg/er/routes/1199522"
        method = "GET"
    }
    ).request()
    println(request.status)

}
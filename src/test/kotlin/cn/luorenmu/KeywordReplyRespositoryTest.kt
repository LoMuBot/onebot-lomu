package cn.luorenmu

import cn.luorenmu.action.commandProcess.eternalReturn.entiy.eternalReturn.EternalReturnNews
import cn.luorenmu.entiy.Request
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to

/**
 * @author LoMu
 * Date 2024.07.04 23:55
 */


fun main() {
    val requestDetailed = Request.RequestDetailed()
    requestDetailed.url = "https://playeternalreturn.com/api/v1/posts/news?page=1&hl=zh-CN"
    requestDetailed.method = "GET"
    val body = RequestController(requestDetailed).request().body()
    println(body)
    println(body.to<EternalReturnNews>())

}

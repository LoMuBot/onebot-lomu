package cn.luorenmu

import cn.luorenmu.entiy.Request
import cn.luorenmu.request.RequestController

/**
 * @author LoMu
 * Date 2025.02.20 14:13
 */
fun main() {
    val request = RequestController(Request.RequestDetailed().apply {
        url = "https://playeternalreturn.com/posts/news/2536"
        method = "GET"
    }
    ).request()
    val body = request.body()
    val regex = "<div class=\"er-article-detail__content er-article-content fr-view\">([\\s\\S]*?)</div>".toRegex()
    val imgRegex = "<img src=\"(.*?)\"".toRegex()
    regex.find(body)?.let {
        it.groups[1]?.let { value ->
            for (matchResult in imgRegex.findAll(value.value)) {
                println(matchResult.groups[1]?.value)
            }
        }
    }

}
package cn.luorenmu.action.request

import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.entiy.Request.RequestParam
import cn.luorenmu.request.RequestController
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.30 21:10
 */
@Component
class DeepSeekRequestData {
    private val baseUrl = "https://api.deepseek.com/chat/completions"
    private var requestHeader: MutableList<RequestParam> = mutableListOf()


    @PostConstruct
    fun init(){
        requestHeader.add(RequestParam().apply {
            name = "Content-Type"
            content = "application/json"
        })

        requestHeader.add(RequestParam().apply {
            name = "Accept"
            content = "application/json"
        })

        requestHeader.add(RequestParam().apply {
            name = "Authorization"
            content = token
        })
    }

    fun chat() {
        RequestController(RequestDetailed().apply {
            url = baseUrl
            method = "Post"
            headers = requestHeader

        })
    }


}
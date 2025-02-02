package cn.luorenmu.action.request

import cn.hutool.http.HttpResponse
import cn.luorenmu.action.request.entiy.DeepSeekMessage
import cn.luorenmu.action.request.entiy.DeepSeekRequest
import cn.luorenmu.config.external.LoMuBotProperties
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
class DeepSeekRequestData(
    private val properties: LoMuBotProperties,
) {
    private val requestHeader: MutableList<RequestParam> = mutableListOf()


    @PostConstruct
    fun init() {
        initRequestHeader(properties.deepSeek.apiKey)
    }

    fun initRequestHeader(key: String) {
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
            content = key
        })
    }

    /**
     *  构建请求
     *  @param  messages 上下文聊天且包括当前聊天
     */
    fun buildRequest(messages: MutableList<DeepSeekMessage>): HttpResponse {
        return RequestController(RequestDetailed().apply {
            url = properties.deepSeek.baseUrl
            method = "Post"
            headers = requestHeader
            bodyJson = DeepSeekRequest.DeepSeekRequestBody.builder(messages)
        }).request()
    }

    fun buildRequest(message: String, role: String, chatHistory: MutableList<DeepSeekMessage>): HttpResponse {
        chatHistory.add(DeepSeekMessage(message, role))
        return buildRequest(chatHistory)
    }
}
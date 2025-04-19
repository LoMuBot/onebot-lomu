package cn.luorenmu.action.request

import cn.hutool.http.HttpResponse
import cn.luorenmu.action.request.entiy.DeepSeekMessage
import cn.luorenmu.action.request.entiy.DeepSeekRequest
import cn.luorenmu.config.external.LoMuBotProperties
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.entiy.Request.RequestParam
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.toJSONString
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.30 21:10
 */
@Component
class DeepSeekRequestData(
    private val properties: LoMuBotProperties,
) {
    private val requestHeader: MutableList<RequestParam> = mutableListOf(RequestParam().apply {
        name = "Content-Type"
        content = "application/json"
    }, RequestParam().apply {
        name = "Accept"
        content = "application/json"
    }, RequestParam().apply {
        name = "Authorization"
        content = if (!properties.deepSeek.apiKey.startsWith("Bearer"))
            "Bearer ${properties.deepSeek.apiKey}"
        else
            properties.deepSeek.apiKey
    })


    /**
     *  构建请求
     *  @param  messages 上下文聊天且包括当前聊天
     */
    fun buildRequest(messages: MutableList<DeepSeekMessage>): HttpResponse {
        return RequestController(RequestDetailed().apply {
            url = properties.deepSeek.baseUrl
            method = "Post"
            headers = requestHeader
            bodyJson = DeepSeekRequest.DeepSeekRequestBody(messages, properties.deepSeek.model).toJSONString()
        }).request()
    }

    fun buildRequest(
        message: String,
        role: String,
        requestBody: (DeepSeekRequest.DeepSeekRequestBody) -> DeepSeekRequest.DeepSeekRequestBody,
    ): HttpResponse {
        return RequestController(RequestDetailed().apply {
            url = properties.deepSeek.baseUrl
            method = "Post"
            headers = requestHeader
            bodyJson = requestBody(DeepSeekRequest.DeepSeekRequestBody(
                mutableListOf(DeepSeekMessage(message, role)),
                properties.deepSeek.model
            )).toJSONString()
        }).request()
    }

    fun buildRequest(
        message: String,
        role: String,
        chatHistory: MutableList<DeepSeekMessage> = mutableListOf(),
    ): HttpResponse {
        chatHistory.add(DeepSeekMessage(message, role))
        return buildRequest(chatHistory)
    }

    fun buildImageRequest(
        message: String,
        image: String,
        role: String,
    ): HttpResponse {
        TODO()
    }
}


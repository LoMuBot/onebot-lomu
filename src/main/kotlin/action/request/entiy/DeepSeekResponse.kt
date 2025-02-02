package cn.luorenmu.action.request.entiy

import cn.hutool.http.HttpResponse
import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2025.02.01 18:28
 */
data class DeepSeekResponse(
    @JSONField(name = "id") val id: String,
    @JSONField(name = "object") val objectType: String,
    @JSONField(name = "created") val created: Long,
    @JSONField(name = "model") val model: String,
    @JSONField(name = "choices") val choices: List<Choice>,
    @JSONField(name = "usage") val usage: Usage,
    @JSONField(name = "system_fingerprint") val systemFingerprint: String,
)

data class Choice(
    @JSONField(name = "index") val index: Int,
    @JSONField(name = "message") val message: DeepSeekMessage,
    @JSONField(name = "logprobs") val logprobs: Any?, // 可以是 null
    @JSONField(name = "finish_reason") val finishReason: String,
)

data class Usage(
    @JSONField(name = "prompt_tokens") val promptTokens: Int,
    @JSONField(name = "completion_tokens") val completionTokens: Int,
    @JSONField(name = "total_tokens") val totalTokens: Int,
    @JSONField(name = "prompt_tokens_details") val promptTokensDetails: PromptTokensDetails,
    @JSONField(name = "prompt_cache_hit_tokens") val promptCacheHitTokens: Int,
    @JSONField(name = "prompt_cache_miss_tokens") val promptCacheMissTokens: Int,
)

data class PromptTokensDetails(
    @JSONField(name = "cached_tokens") val cachedTokens: Int,
)



enum class DeepSeekStatus(val code: Int, val message: String, val solution: String) {
    FORMAT_ERROR(400, "格式错误", "请根据错误信息提示修改请求体"),
    AUTHENTICATION_FAILED(401, "认证失败", "请检查您的 API key 是否正确，如没有 API key，请先 创建 API key"),
    INSUFFICIENT_BALANCE(402, "余额不足", "请确认账户余额，并前往 充值 页面进行充值"),
    PARAMETER_ERROR(422, "参数错误", "请根据错误信息提示修改相关参数"),
    REQUEST_LIMIT_EXCEEDED(429, "请求速率达到上限", "请合理规划您的请求速率"),
    INTERNAL_SERVER_ERROR(500, "服务器故障", "请等待后重试。若问题一直存在，请联系我们解决"),
    SERVER_BUSY(503, "服务器繁忙", "请稍后重试您的请求");

    override fun toString(): String {
        return "ErrorCode(code=$code, message='$message', solution='$solution')"
    }

    companion object {
        fun switch(code: Int): DeepSeekStatus {
            return entries.first { it.code == code }
        }
    }
}

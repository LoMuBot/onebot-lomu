package cn.luorenmu.config.external

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.01 17:28
 */

data class DeepSeek(
    var baseUrl: String = "https://api.deepseek.com/chat/completions",
    var model: String = "deepseek-chat",
    var apiKey: String = ""
)

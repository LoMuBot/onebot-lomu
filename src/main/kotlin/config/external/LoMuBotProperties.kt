package cn.luorenmu.config.external

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.01 17:32
 */
@Component
@ConfigurationProperties("lomu-bot")
data class LoMuBotProperties(
    @NestedConfigurationProperty
    var mail: Mail = Mail(),
    @NestedConfigurationProperty
    var webPool: WebPool = WebPool(),
    @NestedConfigurationProperty
    var deepSeek: DeepSeek = DeepSeek(),
)

package cn.luorenmu.config.external

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.09.01 15:46
 */
@Component
@ConfigurationProperties("mail")
data class Mail(
    var host: String = "smtp.qq.com",
    var port: Int = 465,
    var from: String = "2842775752@qq.com",
    var user: String = "2842775752",
    var pass: String = "123456789",
    var starttls: Boolean = true,
    var ssl: Boolean = false
)
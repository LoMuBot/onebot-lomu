package cn.luorenmu.config.external

/**
 * @author LoMu
 * Date 2024.09.01 15:46
 */
data class Mail(
    var host: String = "smtp.qq.com",
    var port: Int = 465,
    var from: String = "2842775752@qq.com",
    var user: String = "2842775752",
    var pass: String = "123456789",
    var starttls: Boolean = true,
    var ssl: Boolean = false,
)
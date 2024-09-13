package cn.luorenmu.service

import cn.hutool.extra.mail.MailAccount
import cn.hutool.extra.mail.MailUtil
import cn.luorenmu.config.external.Mail
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * @author LoMu
 * Date 2024.09.01 16:24
 */
@Service
class EmailPushService(
    mail: Mail,
) {
    private var lastMsg: String? = null

    private val mailAccount = MailAccount()
        .setHost(mail.host)
        .setPort(mail.port)
        .setFrom(mail.from)
        .setUser(mail.user)
        .setPass(mail.pass)
        .setSslEnable(mail.ssl)
        .setStarttlsEnable(mail.starttls)

    @Async
    fun emailPush(emails: List<String>, title: String, msg: String) {
        lastMsg?.let {
            if (it == msg)
                return
        }
        MailUtil.send(mailAccount, emails, title, msg, true)
        lastMsg = msg
    }

    @Async
    fun emailPush(email: String, title: String, msg: String) {
        emailPush(listOf(email), title, msg)
    }
}


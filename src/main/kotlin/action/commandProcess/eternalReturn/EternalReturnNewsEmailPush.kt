package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.EternalReturnPushRepository
import cn.luorenmu.repository.entiy.EternalReturnPush
import cn.luorenmu.service.EmailPushService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.01.28 13:18
 */
@Component("eternalReturnEmailPush")
class EternalReturnNewsEmailPush(
    private val eternalReturnPushRepository: EternalReturnPushRepository,
    private val emailPushService: EmailPushService,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {

        val email = "${sender.senderId}@qq.com"
        eternalReturnPushRepository.findByEmail(email)?.let {
            return "推送列表中已收录了你的邮箱地址"
        }

        emailPushService.emailPush(
            email, "Test Email", "${sender.senderId} " +
                    "<img src=\"https://i0.hdslb.com/bfs/new_dyn/eafcdc2ea38eddfbb8a0a1f06fe13e6214868240.png\" alt=\"img\">"
        )
        eternalReturnPushRepository.save(
            EternalReturnPush(
                null,
                email,
                sender,
                LocalDateTime.now(),
                sender.groupOrSenderId,
                true
            )
        )

        return "已向你的qq邮件发送了一封测试邮件"
    }

    override fun commandName(): String {
        return "eternalReturnEmailPush"
    }

    override fun state(id: Long): Boolean {
        return true
    }
}
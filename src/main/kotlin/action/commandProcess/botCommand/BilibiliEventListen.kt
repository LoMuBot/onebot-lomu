package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.OneBotConfigRepository
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:58
 */
@Component("BilibiliEventListen")
class BilibiliEventListen(
    private val botCommandControl: BotCommandControl,
    private val configRepository: OneBotConfigRepository,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        if (sender.role.roleNumber >= BotRole.GroupAdmin.roleNumber) {
            return "你没有权限使用这个命令 该命令至少需要群管理员权限"
        }
       return botCommandControl.changeCommandState("BilibiliEventListen", sender)
    }
}

override fun commandName(): String {
    return "BilibiliEventListen"
}
}
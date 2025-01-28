package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:58
 */
@Component("BilibiliEventListen")
class BilibiliEventListenCommand(
    private val botCommandControl: BotCommandControl,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        if (sender.role.roleNumber >= BotRole.GroupAdmin.roleNumber) {
            return "你没有权限使用这个命令 该命令至少需要群管理员权限"
        }
        return botCommandControl.changeCommandState(commandName(), sender)
    }

    override fun state(id: Long) = botCommandControl.commandState(commandName(), id)


    override fun commandName(): String {
        return "BilibiliEventListenCommand"
    }


}
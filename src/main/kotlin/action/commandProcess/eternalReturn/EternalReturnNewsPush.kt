package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.commandProcess.botCommand.BotCommandControl
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 13:18
 */
@Component("eternalReturnGroupPush")
class EternalReturnNewsPush(
    private val botCommandControl: BotCommandControl,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        if (sender.role.roleNumber < BotRole.GroupAdmin.roleNumber) {
            return "你没有权限使用这个命令 该命令至少需要群管理员权限"
        }
        return botCommandControl.changeCommandState(commandName(), sender)
    }

    override fun commandName(): String {
        return "eternalReturnGroupPush"
    }

    override fun state(id: Long): Boolean {
        return botCommandControl.commandState(commandName(), id)
    }
}
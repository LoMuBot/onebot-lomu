package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.BotCommandControl
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 20:32
 */
@Component("ChatStudy")
class ChatStudyCommand(
    private val botCommandControl: BotCommandControl,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        if (true){
            return "该功能已被废弃"
        }
        if (sender.role.roleNumber < BotRole.GroupAdmin.roleNumber) {
            return "你没有权限使用这个命令 该命令至少需要群管理员权限"
        }
        return botCommandControl.changeCommandState(
            commandName(),
            sender
        )
    }

    override fun commandName(): String {
        return "ChatStudy"
    }

    override fun state(id: Long): Boolean {
        return botCommandControl.commandState(commandName(), id) ?: true
    }

    override fun command(): Regex  = Regex("^(聊天学习)$")
    override fun needAtBot(): Boolean = true
}
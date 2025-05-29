package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.BotCommandControl
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.07 04:19
 */
@Component("EmojiGenerationCommand")
class EmojiGenerationCommand(
    private val botCommandControl: BotCommandControl,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        if (sender.role.roleNumber < BotRole.GroupAdmin.roleNumber) {
            return "你没有权限使用这个命令 该命令至少需要群管理员权限"
        }
        return botCommandControl.changeCommandState(
            commandName(),
            sender
        )
    }

    override fun commandName() = "EmojiGenerationCommand"

    override fun state(id: Long): Boolean = botCommandControl.commandState(commandName(), id) ?: false
    override fun command(): Regex = Regex("^(表情生成)$")

    override fun needAtBot(): Boolean = true
}
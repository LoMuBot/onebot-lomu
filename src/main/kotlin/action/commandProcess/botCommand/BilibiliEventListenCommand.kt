package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.BotCommandControl
import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:58
 */
@Component("BilibiliEventListenCommand")
class BilibiliEventListenCommand(
    private val botCommandControl: BotCommandControl,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        if (sender.role.roleNumber < BotRole.GroupAdmin.roleNumber) {
            return null
        }
        return botCommandControl.changeCommandState(commandName(), sender)
    }

    override fun state(id: Long) = botCommandControl.commandState(commandName(), id) ?: false

    override fun commandName(): String {
        return "BilibiliEventListenCommand"
    }

    override fun command(): Regex = Regex("^((视频监听)|(监听视频))$")
    override fun needAtBot(): Boolean = true


}
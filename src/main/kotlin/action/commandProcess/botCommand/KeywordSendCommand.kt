package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 20:33
 */
@Component("KeywordSend")
class KeywordSendCommand(
    private val botCommandControl: BotCommandControl,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        return botCommandControl.changeCommandState(commandName(), sender)
    }

    override fun commandName(): String {
        return "KeywordSend"
    }

    override fun state(id: Long): Boolean {
        return botCommandControl.commandState(commandName(), id)
    }
}
package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.sendMsg
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.30 16:26
 */
@Component("CallBotCommand")
class CallBotCommand(
    private val botContainer: BotContainer,
) : CommandProcess {
    private val response = listOf("螺母在哦~", "(≧ω≦)~", "嗯哼(#^.^#)")
    override fun process(command: String, sender: MessageSender): String? {

        botContainer.getFirstBot().sendMsg(sender.messageType, sender.groupOrSenderId, response.random())
        return null
    }

    override fun commandName() = "CallBotCommand"

    override fun state(id: Long) = true
}
package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.common.extensions.isCQReply
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.29 13:13
 */
@Component("DeleteMessageCommand")
class DeleteMessageCommand(
    val botContainer: BotContainer,
) : CommandProcess {
    val idRegex = "id=(\\d+)".toRegex()
    override fun process(sender: MessageSender): String? {
        if (sender.message.isCQReply())
            idRegex.find(sender.message)?.groups?.get(1)?.value?.let {
                botContainer.robots.values.first().deleteMsg(it.toInt())
            }
        return null
    }

    override fun commandName() = "DeleteMessageCommand"

    override fun state(id: Long) = true
    override fun command(): Regex = Regex("^(撤回)$")
    override fun needAtBot(): Boolean = true
}
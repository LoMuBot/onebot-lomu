package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.render.EternalReturnFindPlayerRender
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:28
 */
@Component("eternalReturnReFindPlayers")
class EternalReturnReFindPlayer(
    private val eternalReturnFindPlayerRender: EternalReturnFindPlayerRender,
    private val botContainer: BotContainer,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        val nickname =
            sender.message.replaceAtToEmpty(sender.botId).trim()
                .replace(command(), "")
                .replaceBlankToEmpty()
                .lowercase()
        botContainer.getFirstBot().setMsgEmojiLike(sender.messageId.toString(), "124")
        eternalReturnFindPlayerRender.asyncSendMessage(nickname, sender.groupOrSenderId, sender.messageId)
        return null
    }

    override fun commandName(): String {
        return "eternalReturnReFindPlayers"
    }

    override fun state(id: Long): Boolean {
        return true
    }

    override fun command(): Regex = Regex("^((玩家|战绩)查询)|(search)")

    override fun needAtBot(): Boolean = false
}
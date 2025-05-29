package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.05.29 17:21
 */
@Component
class EternalReturnRoutes(
    private val web: EternalReturnWebPageScreenshot,
    private val botContainer: BotContainer
) : CommandProcess {


    override fun process(sender: MessageSender): String? {
        val routesId = sender.message.replaceAtToEmpty(sender.botId).trim()
            .replace(command(), "")
            .replaceBlankToEmpty()
            .lowercase()
        botContainer.getFirstBot().setMsgEmojiLike(sender.messageId.toString(), "124")
        return web.webRoutesPageScreenshot(routesId)
    }

    override fun commandName(): String = "EternalReturnRoutes"

    override fun state(id: Long): Boolean = true

    override fun command(): Regex = Regex("^(查询路径)|(routes)")

    override fun needAtBot(): Boolean = false
}
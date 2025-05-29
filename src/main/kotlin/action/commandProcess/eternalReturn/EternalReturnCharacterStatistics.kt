package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.05.29 17:56
 */
@Component
class EternalReturnCharacterStatistics(
    private val web: EternalReturnWebPageScreenshot,
    private val botContainer: BotContainer,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        botContainer.getFirstBot().setMsgEmojiLike(sender.messageId.toString(), "124")
        return web.webCharacterStatisticsPageScreenshot()
    }

    override fun commandName(): String = "EternalReturnCharacterStatistics"

    override fun state(id: Long): Boolean = true

    override fun command(): Regex = Regex("^实验体统计|英雄统计|角色统计|statistics$")

    override fun needAtBot(): Boolean = false
}
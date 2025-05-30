package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.draw.EternalReturnCutoffsDraw
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:24
 */
@Component("eternalReturnCutoffs")
class EternalReturnCutoffs(
    private val eternalReturnDraw: EternalReturnCutoffsDraw,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        return eternalReturnDraw.cutoffs()
    }

    override fun commandName(): String {
        return "eternalReturnCutoffs"
    }

    override fun state(id: Long): Boolean {
        return true
    }

    override fun command(): Regex = Regex("永恒多少分|半神多少分|永恒分段|半神分段|永恒分数|半神分数")

    override fun needAtBot(): Boolean = false
}
package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.draw.EternalReturnCutoffsDraw
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:24
 */
@Component("eternalReturnCutoffs")
class EternalReturnCutoffs(
    private val eternalReturnDraw: EternalReturnCutoffsDraw,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        return eternalReturnDraw.cutoffs()
    }

    override fun commandName(): String {
        return "eternalReturnCutoffs"
    }

    override fun state(id: Long): Boolean {
        return true
    }
}
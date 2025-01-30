package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.common.utils.getImagePath
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.30 16:39
 */
@Component("HelpCommand")
class HelpCommand : CommandProcess {


    override fun process(command: String, sender: MessageSender): String? {
        val imagePath = getImagePath("help")
        return MsgUtils.builder().img(imagePath).build()
    }

    override fun commandName() = "HelpCommand"

    override fun state(id: Long) = true

}
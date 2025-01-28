package cn.luorenmu.action.commandProcess

import cn.luorenmu.listen.entity.MessageSender

/**
 * @author LoMu
 * Date 2025.01.28 13:15
 */
interface CommandProcess {
    fun process(command: String, sender: MessageSender): String?
    fun commandName(): String
}
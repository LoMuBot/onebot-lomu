package cn.luorenmu.action.commandProcess

import cn.luorenmu.listen.entity.MessageSender

/**
 * @author LoMu
 * Date 2025.01.28 13:15
 */
interface CommandProcess {
    /**
     * 命令处理
     *
     * @param sender
     * @return
     */
    fun process(sender: MessageSender): String?

    /**
     * 命令名称
     *
     * @return
     */
    fun commandName(): String

    /**
     * 命令启用状态
     *
     * @param id
     * @return
     */
    fun state(id: Long): Boolean


    /**
     * 命令匹配 正则
     */
    fun command(): Regex

    /**
     *  需要at Bot
     */
    fun needAtBot(): Boolean
}
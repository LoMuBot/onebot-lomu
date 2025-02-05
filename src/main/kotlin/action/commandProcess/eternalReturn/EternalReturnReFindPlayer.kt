package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.common.extensions.replaceAtToBlank
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:28
 */
@Component("eternalReturnReFindPlayers")
class EternalReturnReFindPlayer(
    private val redisTemplate: StringRedisTemplate,
    private val applicationContext: ApplicationContext,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        val nickname =
            sender.message.replaceAtToBlank(sender.botId).trim()
                .replace(Regex(command), "")
                .replaceBlankToEmpty()
                .lowercase()
        redisTemplate.delete("Eternal_Return_NickName:$nickname")
        val findPlayer = applicationContext.getBean("eternalReturnFindPlayers") as CommandProcess
        return findPlayer.process(command, sender)
    }

    override fun commandName(): String {
        return "eternalReturnReFindPlayers"
    }

    override fun state(id: Long): Boolean {
        return true
    }
}
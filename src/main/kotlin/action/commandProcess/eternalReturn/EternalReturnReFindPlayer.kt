package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.listen.entity.MessageSender
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * @author LoMu
 * Date 2025.01.28 14:28
 */
class EternalReturnReFindPlayer(
    private val redisTemplate: StringRedisTemplate,
): CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        val nickname = ""
        redisTemplate.delete("Eternal_Return_NickName:$nickname")
        TODO("Not yet implemented")
    }

    override fun commandName(): String {
        return "eternalReturnReFindPlayers"
    }
}
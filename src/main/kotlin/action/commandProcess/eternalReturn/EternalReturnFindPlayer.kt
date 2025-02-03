package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.extensions.sendGroupMsg
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2025.01.28 13:42
 */
@Component("eternalReturnFindPlayers")
class EternalReturnFindPlayer(
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
    private val redisTemplate: StringRedisTemplate,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        val nickname =
            sender.message.replaceAtToEmpty(sender.botId).trim()
                .replace(Regex(command), "")
                .replaceBlankToEmpty()
                .lowercase()

        // check name rule
        if (nickname.isBlank() || nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        val opsForValue = redisTemplate.opsForValue()

        // check cache
        opsForValue["Eternal_Return_NickName:$nickname"]?.let {
            return "$it \n该数据由缓存命中"
        }


        // check name exist and sync data
        if (!eternalReturnRequestData.findExistPlayers(nickname)) {
            val notFound = MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
            opsForValue["Eternal_Return_NickName:$nickname", notFound, 7L] = TimeUnit.DAYS
            return notFound
        }

        return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)
    }

    override fun commandName(): String {
        return "eternalReturnFindPlayers"
    }

    override fun state(id: Long): Boolean {
        return true
    }
}
package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
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
    private val botContainer: BotContainer,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        println(sender.message)
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
            if (it.contains("不存在的玩家 -> ")) {
                return "不存在的玩家 -> $nickname\n该数据由缓存命中 如果角色已存在请使用重新查询(重新查询玩家 xxx)"
            }
            return it
        }


        // check name exist and sync data
        if (!eternalReturnRequestData.findExistPlayers(nickname)) {
            val notFound = MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
            opsForValue["Eternal_Return_NickName:$nickname", notFound, 7L] = TimeUnit.DAYS
            return notFound
        }
        botContainer.getFirstBot().sendGroupMsg(
            sender.groupOrSenderId,
            MsgUtils.builder().reply(sender.messageId)
                .text("喵！螺母这就去帮主人查角色喵～稍等一下下哦，螺母会尽快把结果告诉主人的喵！(≧ω≦)/").build(),
            false
        )
        return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)
    }

    override fun commandName(): String {
        return "eternalReturnFindPlayers"
    }

    override fun state(id: Long): Boolean {
        return true
    }
}
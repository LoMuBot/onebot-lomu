package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2025.01.28 13:42
 */
class EternalReturnFindPlayer(
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
    private val redisTemplate: StringRedisTemplate,
) : CommandProcess {
    override fun process(command: String, sender: MessageSender): String? {
        val nickname = ""
        // check name rule
        if (nickname.isBlank() || nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        val opsForValue = redisTemplate.opsForValue()

        // check cache
        val nicknameData = opsForValue["Eternal_Return_NickName:$nickname"]
        if (nicknameData != null) {
            if (nicknameData.contains("不存在的玩家")) {
                return "$nickname\n 该数据由缓存命中 如果角色已存在请使用重新查询(重新查询玩家 xxx)"
            }
            return nicknameData
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
}
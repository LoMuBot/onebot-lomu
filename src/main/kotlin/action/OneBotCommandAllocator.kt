package cn.luorenmu.action

import cn.luorenmu.entiy.OneBotAllCommands
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.OneBotCommandRespository
import cn.luorenmu.repository.entiy.OneBotCommand
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.07.13 1:52
 */
@Component
class OneBotCommandAllocator(
    private val oneBotCommandRespository: OneBotCommandRespository,
    private val redisTemplate: StringRedisTemplate,
    private val permissionsManager: PermissionsManager,

) {


    private fun isCurrentCommand(
        botId: Long,
        command: String,
        commandName: String,
        oneBotCommand: OneBotCommand,
    ): Boolean {
        val atMe = MsgUtils.builder().at(botId).build()
        val removeAtAndEmptyCharacterCommand = command.replace(atMe, "").replace(" ", "")
        if (oneBotCommand.needAtMe) {
            if (!command.contains(atMe)) {
                return false
            }
        }
        oneBotCommand.needAdmin?.let {
            if (!it) {
                return false
            }
        }
        return oneBotCommand.commandName == commandName && removeAtAndEmptyCharacterCommand.contains(Regex(oneBotCommand.keyword))
    }


    fun process(bot: Bot, messageSender: MessageSender): String? {
        val botId = bot.selfId
        val senderId = messageSender.senderId
        val allCommands = redisTemplate.opsForValue()["allCommands"]?.to<OneBotAllCommands>()?.allCommands ?: run {
            synchronized(redisTemplate) {
                // 二次安全检查
                redisTemplate.opsForValue()["allCommands"]?.to<OneBotAllCommands>()?.allCommands ?: run {
                    val allCommands = oneBotCommandRespository.findAll()
                    val oneBotCommands = OneBotAllCommands(allCommands)
                    redisTemplate.opsForValue()["allCommands", oneBotCommands.toJSONString(), 1L] =
                        TimeUnit.DAYS
                    allCommands
                }
            }
        }


        allCommands.firstOrNull { isCurrentCommand(botId, messageSender.message, it.commandName, it) }
            ?.let { oneBotCommand ->
                val command = messageSender.message.replace(MsgUtils.builder().at(botId).build(), "")
                return when (oneBotCommand.commandName) {

                    "botCommandBanStudy" -> permissionsManager.banStudy(
                        messageSender.groupOrSenderId,
                        messageSender.role,
                        senderId
                    )

                    "botCommandUnbanStudy" -> permissionsManager.unbanStudy(
                        messageSender.groupOrSenderId,
                        messageSender.role,
                        senderId
                    )

                    "botCommandBanKeyword" -> permissionsManager.banKeyword(
                        messageSender.groupOrSenderId,
                        messageSender.role,
                        senderId
                    )

                    "botCommandUnbanKeyword" -> permissionsManager.unbanKeyword(
                        messageSender.groupOrSenderId,
                        messageSender.role,
                        senderId
                    )

                    "BilibiliEventListen" -> permissionsManager.bilibiliEventListen(
                        messageSender.groupOrSenderId,
                        messageSender.role,
                        senderId
                    )

                    "banBilibiliEventListen" -> permissionsManager.banBilibiliEventListen(
                        messageSender.groupOrSenderId,
                        messageSender.role,
                        senderId
                    )

                    else -> null
                }
            }

        return null
    }


}
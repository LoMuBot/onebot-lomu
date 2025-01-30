package cn.luorenmu.action


import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.common.extensions.isCQReply
import cn.luorenmu.entiy.OneBotAllCommands
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.OneBotCommandRespository
import cn.luorenmu.repository.entiy.OneBotCommand
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationContext
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
    private val applicationContext: ApplicationContext,
) {
    private val log = KotlinLogging.logger {}

    private fun isCurrentCommand(
        botId: Long,
        command: String,
        oneBotCommand: OneBotCommand,
    ): Boolean {
        val atMe = MsgUtils.builder().at(botId).build()
        var removeAtAndEmptyCharacterCommand = command.replace(atMe, "").replace(" ", "")
        if (oneBotCommand.needAtMe) {
            if (!command.contains(atMe)) {
                return false
            }
        }

        if (removeAtAndEmptyCharacterCommand.isCQReply()) {
            removeAtAndEmptyCharacterCommand =
                removeAtAndEmptyCharacterCommand.replace("\\[CQ:reply,id=\\d+]".toRegex(), "")
        }

        return removeAtAndEmptyCharacterCommand.contains(Regex(oneBotCommand.keyword))
    }


    fun process(bot: Bot, messageSender: MessageSender): String? {
        val botId = bot.selfId
        allCommands().firstOrNull { isCurrentCommand(botId, messageSender.message, it) }
            ?.let { oneBotCommand ->
                val commandProcess = applicationContext.getBean(oneBotCommand.commandName) as CommandProcess
                try {
                    return commandProcess.process(oneBotCommand.keyword, messageSender)
                } catch (e: Exception) {
                    log.error { e.stackTraceToString() }
                    return "服务器内部发生错误来自功能${commandProcess.commandName()}\n Error: ${e.message}"
                }
            }
        return null
    }


    fun allCommands(): List<OneBotCommand> =
        redisTemplate.opsForValue()["allCommands"]?.to<OneBotAllCommands>()?.allCommands ?: run {
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
}
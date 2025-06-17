package cn.luorenmu.action.commandProcess

import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.isCQReply
import cn.luorenmu.common.extensions.sendGroupMsg
import cn.luorenmu.common.extensions.sendMsg
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.exception.LoMuBotException
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.listen.entity.MessageType
import cn.luorenmu.repository.CommandUseHistoryRepository
import cn.luorenmu.repository.entity.CommandUseHistory
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.13 1:52
 */
@Component
class OneBotCommandAllocator(
    applicationContext: ApplicationContext,
    private val bot: BotContainer,
    private val redisUtils: RedisUtils,
    private val commandUseHistoryRepository: CommandUseHistoryRepository,
) {
    private val log = KotlinLogging.logger {}

    private val commandList: List<CommandProcess> =
        applicationContext.getBeansOfType(CommandProcess::class.java).values.toList()

    private fun isCurrentCommand(
        botId: Long,
        command: String,
        oneBotCommand: CommandProcess,
    ): Boolean {
        val atMe = MsgUtils.builder().at(botId).build()
        var removeAtAndEmptyCharacterCommand = command.replace(atMe, "").replace(" ", "")
        if (oneBotCommand.needAtBot()) {
            if (!command.contains(atMe)) {
                return false
            }
        }

        // 移除回复
        if (removeAtAndEmptyCharacterCommand.isCQReply()) {
            removeAtAndEmptyCharacterCommand =
                removeAtAndEmptyCharacterCommand.replace("\\[CQ:reply,id=\\d+]".toRegex(), "")
        }

        return removeAtAndEmptyCharacterCommand.contains(oneBotCommand.command())
    }

    private fun send(message: String?, id: Long, messageId: Int, type: MessageType) {
        message?.let {
            if (it.isNotBlank()) {
                bot.getFirstBot().sendMsg(
                    type,
                    id,
                    MsgUtils.builder().reply(messageId).text(it).build(),
                )
            }
        }
    }

    @Async
    fun process(bot: Bot, messageSender: MessageSender) {
        val botId = bot.selfId
        commandList.firstOrNull { isCurrentCommand(botId, messageSender.message, it) }
            ?.let { oneBotCommand ->
                try {
                    commandUseHistoryRepository.save(
                        CommandUseHistory(
                            senderInfo = messageSender,
                            commandName = oneBotCommand.commandName()
                        )
                    )
                    send(
                        oneBotCommand.process(messageSender),
                        messageSender.groupOrSenderId,
                        messageSender.messageId,
                        messageSender.messageType
                    )
                } catch (e: LoMuBotException) {
                    send(
                        e.msg,
                        messageSender.groupOrSenderId,
                        messageSender.messageId,
                        messageSender.messageType
                    )
                } catch (e: Exception) {
                    log.error { e.stackTraceToString() }
                    redisUtils.setCache(
                        "EternalReturn: ${messageSender.message}",
                        "${e.javaClass}:${e.stackTraceToString()}-${e.message}",
                        0
                    )
                    bot.sendGroupMsg(646708986, "${e.javaClass}:出现错误")
                    send(
                        "服务器内部发生错误来自功能${oneBotCommand.commandName()}\n ",
                        messageSender.groupOrSenderId,
                        messageSender.messageId,
                        messageSender.messageType
                    )
                }
            }
    }


}

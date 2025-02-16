package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.commandProcess.botCommand.EmojiGenerationCommand
import cn.luorenmu.action.petpet.PetpetGenerate
import cn.luorenmu.action.petpet.TemplateRegister
import cn.luorenmu.common.extensions.*
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.05 11:27
 */
@Component
class PetpetListen(
    private val petpetGenerate: PetpetGenerate,
    private val botContainer: BotContainer,
    private val emojiGenerationCommand: EmojiGenerationCommand,
) {
    private val blackList = listOf("口我", "撅")

    @Async
    fun process(messageSender: MessageSender) {
        if (!messageSender.message.startsWith("/")) {
            return
        }

        val replaceAt = messageSender.message.substring(1).replaceAtToBlank().replaceBlankToEmpty()
        if (blackList.contains(replaceAt) && !(messageSender.role.roleNumber >= BotRole.GroupAdmin.roleNumber ||
                    emojiGenerationCommand.state(messageSender.groupOrSenderId))
        ) {
            return
        }
        TemplateRegister.getTemplate(replaceAt)?.let {
            if (messageSender.message.isAt(messageSender.botId)) {
                TemplateRegister.getTemplate("抽打")?.let { lash ->
                    val path =
                        petpetGenerate.generate(
                            lash,
                            messageSender.senderId.toString(),
                            messageSender.botId.toString(),
                        )
                    botContainer.getFirstBot()
                        .sendGroupMsg(messageSender.groupOrSenderId, MsgUtils.builder().img(path).build())
                }
                return
            }

            val path =
                petpetGenerate.generate(
                    it,
                    messageSender.message.getAtQQ(0) ?: messageSender.senderId.toString(),
                    messageSender.message.getAtQQ(1) ?: messageSender.senderId.toString(),
                )
            botContainer.getFirstBot()
                .sendGroupMsg(messageSender.groupOrSenderId, MsgUtils.builder().img(path).build())

        }
    }
}
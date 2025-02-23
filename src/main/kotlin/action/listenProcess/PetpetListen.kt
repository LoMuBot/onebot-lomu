package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.PermissionsManager
import cn.luorenmu.action.commandProcess.botCommand.EmojiGenerationCommand
import cn.luorenmu.action.petpet.PetpetGenerate
import cn.luorenmu.action.petpet.TemplateRegister
import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.common.extensions.*
import cn.luorenmu.config.shiro.customAction.getImage
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.listen.entity.MessageType
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import com.mikuac.shiro.dto.action.response.GetMsgResp
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
    private val permissionsManager: PermissionsManager,
    private val qqRequestData: QQRequestData,
) {
    private val blackList = listOf("口我", "撅")

    @Async("asyncProcessThreadPool")
    fun process(messageSender: MessageSender) {
        if (!messageSender.message.replaceReplyToEmpty().startsWith("/")) {
            return
        }
        val replaceCQMessage =
            messageSender.message.replace("/", "").replaceAtToEmpty().replaceBlankToEmpty()
                .replaceReplyToEmpty().replaceImageToEmpty()
        if (blackList.contains(replaceCQMessage) && !(messageSender.role.roleNumber >= BotRole.GroupAdmin.roleNumber ||
                    emojiGenerationCommand.state(messageSender.groupOrSenderId))
        ) {
            return
        }
        TemplateRegister.getTemplate(replaceCQMessage)?.let {
            // bot反射表情
            if (messageSender.message.isAt(messageSender.botId)) {
                reflectionTarget(messageSender)
                return
            }
            val existsFrom = it.elements.toString().contains("from")
            val triggerTo = triggerObj(messageSender, 0, existsFrom)
            val triggerFrom = triggerObj(messageSender, 1, existsFrom)
            if (triggerTo == null || triggerFrom == null) {
                reflectionTarget(messageSender)
                return
            }
            val path =
                petpetGenerate.generate(
                    it,
                    triggerTo,
                    triggerFrom,
                    messageSender.message.replaceAtToEmpty().replaceReplyToEmpty()
                )
            botContainer.getFirstBot()
                .sendGroupMsg(messageSender.groupOrSenderId, MsgUtils.builder().img(path).build())

        }
    }

    private fun reflectionTarget(messageSender: MessageSender) {
        TemplateRegister.getTemplate("抽打")?.let { lash ->
            val path =
                petpetGenerate.generate(
                    lash,
                    qqRequestData.downloadQQAvatar(messageSender.senderId.toString()),
                    qqRequestData.downloadQQAvatar(messageSender.botId.toString()),
                )
            botContainer.getFirstBot()
                .sendGroupMsg(messageSender.groupOrSenderId, MsgUtils.builder().img(path).build())
        }
        return
    }

    /**
     *  @param index to表示0 from表示1
     *  @return 如果为null表示触发对象为机器人
     */
    private fun triggerObj(
        messageSender: MessageSender,
        index: Int,
        existsFrom: Boolean,
    ): String? {
        // 就是不允许对bot使用表情
        if (messageSender.senderId == messageSender.botId || messageSender.message.isAt(messageSender.botId)) {
            return null
        }

        // 模版存在from并且当前为from的情况下 则是第一条at或第二条at
        if (existsFrom && index == 1) {
            messageSender.message.getAtQQ(1)?.let {
                return qqRequestData.downloadQQAvatar(it)
            }
            return qqRequestData.downloadQQAvatar(messageSender.senderId.toString())
        }
        // 回复消息
        messageSender.message.getCQReplyMessageId()?.let {
            val msg = botContainer.getFirstBot().getMsg(it.toInt()).data
            return triggerObj(getMsgToMessageSender(msg, messageSender), index, existsFrom = true)
        }

        // 自己发送的图片
        messageSender.message.getCQFileStr()?.let {
            return botContainer.getFirstBot().getImage(it).data.file
        } ?: run {
            // 回复的图片
            messageSender.message.getFileStr()?.let {
                return botContainer.getFirstBot().getImage(it).data.file
            }
        }

        // 当前消息为to为at的目标或自己

        messageSender.message.getAtQQ(0)?.let {
            return qqRequestData.downloadQQAvatar(it)
        }

        return qqRequestData.downloadQQAvatar(messageSender.senderId.toString())
    }


    private fun getMsgToMessageSender(msg: GetMsgResp, messageSender: MessageSender) = MessageSender(
        groupOrSenderId = messageSender.groupOrSenderId,
        senderName = msg.sender.nickname,
        senderId = msg.sender.userId.toLong(),
        role = permissionsManager.botRole(msg.sender.userId.toLong(), msg.sender.role),
        message = msg.message,
        // TODO 后续如果支持Private时应当修改
        messageType = MessageType.convert("group"),
        messageId = msg.messageId,
        botId = messageSender.botId
    )
}
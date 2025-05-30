package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.render.EternalReturnFindPlayerRender
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.extensions.sendGroupMsg
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 13:42
 */
@Component("eternalReturnFindPlayers")
class EternalReturnFindPlayer(
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val eternalReturnWebPageScreenshot: EternalReturnWebPageScreenshot,
    private val botContainer: BotContainer,
    private val eternalReturnFindPlayerRender: EternalReturnFindPlayerRender,
    @Value("\${server.port}")
    private val port: String,
) : CommandProcess {

    override fun process(sender: MessageSender): String? {
        var nickname =
            sender.message.replaceAtToEmpty(sender.botId).trim()
                .replace(command(), "")
                .replaceBlankToEmpty()
                .lowercase()


        // check name rule
        if (nickname.isBlank()) {
            nickname = sender.senderName
        }
        if (nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        if (!eternalReturnRequestData.syncPlayers(nickname)) {
            return MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
        }
        botContainer.getFirstBot().setMsgEmojiLike(sender.messageId.toString(), "124")
        try {
            return eternalReturnWebPageScreenshot.webPlayerPageScreenshot(nickname)
        } catch (e: Exception) {
            botContainer.getFirstBot()
                .sendGroupMsg(
                    sender.groupOrSenderId,
                    MsgUtils.builder().reply(sender.messageId).text("与服务器无法正常连接 正在重试")
                        .build()
                )
            return eternalReturnFindPlayerRender.imageRenderGenerate(nickname)
        }
    }


    override fun commandName(): String {
        return "eternalReturnFindPlayers"
    }

    override fun state(id: Long): Boolean {
        return true
    }

    override fun command(): Regex = Regex("^((查詢(玩家|戰績))|((玩家|戰績)查詢)|(查询(玩家|战绩)))")

    override fun needAtBot(): Boolean = false
}
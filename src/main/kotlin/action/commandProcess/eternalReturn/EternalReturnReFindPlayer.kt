package cn.luorenmu.action.commandProcess.eternalReturn

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.render.EternalReturnFindPlayerRender
import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.replaceAtToEmpty
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.config.shiro.customAction.setMsgEmojiLike
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.28 14:28
 */
@Component("eternalReturnReFindPlayers")
class EternalReturnReFindPlayer(
    private val eternalReturnFindPlayerRender: EternalReturnFindPlayerRender,
    private val botContainer: BotContainer,
    private val eternalReturnRequestData: EternalReturnRequestData,
    private val redisUtils: RedisUtils,
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        val nickname =
            sender.message.replaceAtToEmpty(sender.botId).trim()
                .replace(command(), "")
                .replaceBlankToEmpty()
                .lowercase()
        if (!eternalReturnRequestData.syncPlayers(nickname)) {
            return MsgUtils.builder().text("不存在的玩家 -> $nickname").build()
        }
        if (nickname.contains("@") || nickname.length < 2) {
            return MsgUtils.builder().text("名称不合法 -> $nickname").build()
        }

        botContainer.getFirstBot().setMsgEmojiLike(sender.messageId.toString(), "124")
        redisUtils.getCache("nickname:${nickname}", String::class.java)?.let {
            return it
        }
        return eternalReturnFindPlayerRender.imageRenderGenerate(nickname)
    }

    override fun commandName(): String {
        return "查询玩家"
    }

    override fun state(id: Long): Boolean {
        return true
    }

    override fun command(): Regex = Regex("^(((玩家|战绩)查询)|(search)|(查询玩家)|(查询战绩))")

    override fun needAtBot(): Boolean = false
}
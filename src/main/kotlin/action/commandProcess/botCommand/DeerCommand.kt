package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.CommandProcess
import cn.luorenmu.action.draw.DeerDraw
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.DeerRepository
import cn.luorenmu.repository.entiy.Deer
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.02.21 11:08
 */
@Component("DeerCommand")
class DeerCommand(
    private val deerDraw: DeerDraw,
    private val deerRepository: DeerRepository,
    private val emojiGenerationCommand: EmojiGenerationCommand
) : CommandProcess {
    override fun process(sender: MessageSender): String? {
        if (!emojiGenerationCommand.state(sender.groupOrSenderId)){
            return null
        }
        val nowYear = LocalDateTime.now().year
        val nowMonth = LocalDateTime.now().monthValue
        val nowDay = LocalDateTime.now().dayOfMonth
        val deerDays =
            deerRepository.findBySenderIdAndYearAndMonth(sender.senderId, nowYear, nowMonth)?.let {
                if (!it.days.contains(nowDay)) {
                    it.days.add(nowDay)
                    it.count = it.count?.plus(1) ?: it.days.size
                    deerRepository.save(it)
                }
                it
            } ?: run {
                val deer = Deer(
                    null,
                    sender.senderId,
                    nowYear,
                    nowMonth,
                    1,
                    mutableListOf(nowDay)
                )
                deerRepository.save(deer)
                deer
            }

        return MsgUtils.builder().reply(sender.messageId)
            .text("享受\uD83E\uDD8C生活,\uD83D\uDC2E\uD83D\uDC2E快乐每一天")
            .img(OneBotMedia().file(deerDraw.drawDeerKing(deerDays, sender))).build()
    }

    override fun commandName() = "DeerCommand"

    override fun state(id: Long) = true
    override fun command(): Regex  = Regex("^(\uD83E\uDD8C)$")
    override fun needAtBot(): Boolean = false
}
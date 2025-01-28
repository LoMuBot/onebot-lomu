package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.draw.DeerDraw
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.DeerRepository
import cn.luorenmu.repository.entiy.Deer
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.OneBotMedia
import com.mikuac.shiro.core.Bot
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.01.07 02:30
 */
@Component
class DeerListen(
    private val deerDraw: DeerDraw,
    private val deerRepository: DeerRepository,
) {

    fun process(bot: Bot, commandSender: MessageSender) {
        if ("\uD83E\uDD8C".uppercase() == commandSender.message.uppercase()) {
            val nowYear = LocalDateTime.now().year
            val nowMonth = LocalDateTime.now().monthValue
            val nowDay = LocalDateTime.now().dayOfMonth
            val deerDays =
                deerRepository.findBySenderIdAndYearAndMonth(commandSender.senderId, nowYear, nowMonth)?.let {
                    if (!it.days.contains(nowDay)) {
                        it.days.add(nowDay)
                        deerRepository.save(it)
                    }
                    it
                } ?: run {
                    val deer = Deer(
                        null,
                        commandSender.senderId,
                        nowYear,
                        nowMonth,
                        mutableListOf(nowDay)
                    )
                    deerRepository.save(deer)
                    deer
                }

            bot.sendGroupMsg(
                commandSender.groupOrSenderId,
                MsgUtils.builder().reply(commandSender.messageId)
                    .text("享受\uD83E\uDD8C生活,\uD83D\uDC2E\uD83D\uDC2E快乐每一天")
                    .img(OneBotMedia().file(deerDraw.drawDeerKing(deerDays, commandSender))).build(), false
            )
        }
    }


}

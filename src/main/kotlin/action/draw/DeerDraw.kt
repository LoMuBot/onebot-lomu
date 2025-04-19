package cn.luorenmu.action.draw

import cn.luorenmu.action.draw.entity.DeerRank
import cn.luorenmu.action.listenProcess.entity.DeerSender
import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.common.utils.DrawImageUtils
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.DeerRepository
import cn.luorenmu.repository.entiy.Deer
import org.springframework.stereotype.Component
import java.awt.Color
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * @author LoMu
 * Date 2025.01.28 14:40
 */
@Component
class DeerDraw(
    private val deerRepository: DeerRepository,
    private val qqRequestData: QQRequestData,
) {
    private fun ranking(senderId: Long): DeerRank {
        val nowYear = LocalDateTime.now().year
        val nowMonth = LocalDateTime.now().monthValue
        val all = deerRepository.findByYearAndMonth(nowYear, nowMonth)
        val listDeer = all.map {
            DeerSender(it.senderId, it.days.size)
        }.toMutableList()
        val sender = listDeer.first { it.id == senderId }
        // 同排名数量的人数
        val rankingCount = listDeer.count { it.count == sender.count } - 1
        // 移除所有同一排名
        listDeer.removeAll { it.count == sender.count }
        val ranking = listDeer.count { it.count > sender.count }
        return DeerRank(ranking + 1, rankingCount)
    }

    fun isLastMonthKing(senderId: Long): Int {
        var year = LocalDateTime.now().year
        var month = LocalDateTime.now().monthValue
        if (month == 1) {
            year -= 1
            month = 12
        } else {
            month -= 1
        }
        val all = deerRepository.findByYearAndMonth(year, month)
        val max = all.maxBy { it.days.count() }.days.count()
        val deerKings = all.filter { max == it.days.count() }
        deerKings.firstOrNull { it.senderId == senderId }?.let {
            return it.days.count()
        }
        return -1
    }

    fun drawDeerKing(deer: Deer, commandSender: MessageSender): String {

        val avatarPath = qqRequestData.downloadQQAvatar(commandSender.senderId.toString())

        val drawImageUtils = DrawImageUtils.builder()
        drawImageUtils.setTemplate(ReadWriteFile.currentPathFileName("image/deerTemplate.jpg"))

        val deerDate = deer.days
        drawImageUtils.drawString(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月")),
            Color.black,
            480,
            50,
            30
        )
        drawImageUtils.drawImage(avatarPath, 450, 800, 200, 200, null)

        if (isLastMonthKing(commandSender.senderId) > 0) {
            drawImageUtils.drawString("${commandSender.senderName}[DeerKing]", Color.red, 450, 780, 30)
        } else {
            drawImageUtils.drawString(commandSender.senderName, Color.black, 450, 780, 30)
        }

        val deerSenderCount = deerRepository.findByYearAndMonth(LocalDateTime.now().year, LocalDateTime.now().monthValue).count()
        val ranking = ranking(commandSender.senderId)
        drawImageUtils.drawString(
            "当月在${deerSenderCount}人中 排名第${ranking.ranking} 名与${ranking.rankingCount}人位于同一名次",
            Color.black,
            250,
            1050,
            30
        )
        val monthDays = YearMonth.now().lengthOfMonth()
        var dateDay = 1
        for (i in 0..4) {
            for (j in 0..6) {
                if (deerDate.contains(dateDay)) {
                    drawImageUtils.drawImage(
                        ReadWriteFile.currentPathFileName("image/fuckdeer.jpg"),
                        50 + 150 * j,
                        100 + 130 * i,
                        100,
                        100,
                        null
                    )
                } else {
                    drawImageUtils.drawImage(
                        ReadWriteFile.currentPathFileName("image/deer.jpg"),
                        50 + 150 * j,
                        100 + 130 * i,
                        100,
                        100,
                        null
                    )
                }

                drawImageUtils.drawString("$dateDay.", Color.black, 50 + 150 * j, 100 + 130 * i, 20)
                dateDay++
                if (dateDay > monthDays) {
                    break
                }
            }
            if (dateDay > monthDays) {
                break
            }
        }

        val returnMsg = ReadWriteFile.currentPathFileName("image/qq/deer/${commandSender.senderId}.png").substring(1)
        drawImageUtils.saveImage(returnMsg)
        return returnMsg
    }
}
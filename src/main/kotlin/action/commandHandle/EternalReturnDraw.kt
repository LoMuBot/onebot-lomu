package cn.luorenmu.action.commandHandle

import cn.luorenmu.common.utils.DrawImageUtils
import cn.luorenmu.common.utils.getEternalReturnDataImagePath
import cn.luorenmu.common.utils.getEternalReturnImagePath
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.awt.Color
import java.awt.Font
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2024.08.03 9:26
 */
@Component
class EternalReturnDraw(
    private val redisTemplate: RedisTemplate<String, String>,
    private val eternalReturnRequestData: EternalReturnRequestData,
) {
    private fun doubleToPercentage(value: Double): String {
        return "%.1f%%".format(value * 100)
    }

    fun cutoffs(): String {
        val tierDistributions = eternalReturnRequestData.tierDistributionsFind()
        val opsForValue = redisTemplate.opsForValue()
        tierDistributions?.let { td ->
            eternalReturnRequestData.leaderboardFind()?.let { leaderboard ->
                // 段位
                val tierTypes = arrayOf(1, 2, 3, 4, 5, 6, 66, 7, 8)

                val count = mutableMapOf<Int, Int>()
                val rate = mutableMapOf<Int, Double>()


                // 收集整个段位的人数和占率
                for (distribution in td.distributions) {
                    count[distribution.tierType]?.let {
                        count[distribution.tierType] = it + distribution.count
                    } ?: run {
                        count[distribution.tierType] = distribution.count
                    }
                    rate[distribution.tierType]?.let {
                        rate[distribution.tierType] = it + distribution.rate
                    } ?: run {
                        rate[distribution.tierType] = distribution.rate
                    }
                }

                // 永恒/半神
                val eternal = leaderboard.cutoffs[0].mmr
                val demigod = leaderboard.cutoffs[1].mmr


                //画上
                synchronized(redisTemplate) {
                    opsForValue["Eternal_Return:cutoffs"]?.let { redisData ->
                        return redisData
                    }
                    val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss"))
                    val draw = DrawImageUtils.builder()
                    draw.setTemplate(getEternalReturnDataImagePath("bg-character.jpg"))
                    draw.setFont("微软雅黑", Font.PLAIN)
                    draw.drawString("正式赛季 S4排名", Color.WHITE, 40, 50, 20)
                    draw.drawString("Based on DAK.GG Data.", Color.orange, 45, 70, 10)
                    draw.drawString("最近更新: $date (30分钟后更新)", Color.gray, 40, 90, 12)
                    draw.drawImage(getEternalReturnDataImagePath("tier/8.png"), 40, 110, 30, 30, null)
                    draw.drawString(eternal.toString(), Color.white, 80, 130, 13)
                    draw.drawImage(getEternalReturnDataImagePath("tier/7.png"), 140, 110, 30, 30, null)
                    draw.drawString(demigod.toString(), Color.white, 180, 130, 13)

                    val height: Int = draw.height
                    val h: Int = height / 3
                    var x = 500
                    var num = 1
                    for (i in 1..8) {
                        if ((h * (num - 1) + 20) > height) {
                            x += 200
                            num = 1
                        }

                        draw.drawImage(
                            getEternalReturnDataImagePath("tier/${tierTypes[i]}.png"),
                            x,
                            h * (num - 1) + 20,
                            20,
                            20,
                            null
                        )
                        draw.drawString(
                            "${count[i]}人(${doubleToPercentage(rate[i]!!)})",
                            Color.white,
                            x + 30,
                            h * (num - 1) + 33,
                            13
                        )
                        num++
                    }
                    val url = getEternalReturnImagePath("cutoffs.png")
                    draw.saveImage(url)
                    val cqImg = MsgUtils.builder().img(url).build()
                    opsForValue["Eternal_Return:cutoffs", cqImg, 30L] = TimeUnit.MINUTES
                    return cqImg
                }
            }
        }
        return "连接到dakgg的远程服务器出现错误"
    }
}
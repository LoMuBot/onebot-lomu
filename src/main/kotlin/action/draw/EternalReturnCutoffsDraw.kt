package cn.luorenmu.action.draw


import cn.luorenmu.action.request.EternalReturnRequestData
import cn.luorenmu.common.utils.DrawImageUtils
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.common.utils.getEternalReturnDataImagePath
import cn.luorenmu.common.utils.getEternalReturnImagePath
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Component
import java.awt.Color
import java.awt.Font
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * @author LoMu
 * Date 2024.08.03 9:26
 */
@Component
class EternalReturnCutoffsDraw(
    private val redisUtils: RedisUtils,
    private val eternalReturnRequestData: EternalReturnRequestData,
) {
    private fun doubleToPercentage(value: Double, i: Int): String {
        return "%.1f".format(value * i)
    }

    fun draw(): String {
        val tierDistributions = eternalReturnRequestData.tierDistributionsFind()
        tierDistributions?.let { td ->
            eternalReturnRequestData.leaderboardFind()?.let { leaderboard ->
                // 段位
                val tierTypes = td.distributions.stream().map { ds -> ds.tierType }.distinct().sorted { o1, o2 ->
                    val i1 = if (o1 < 10) o1 * 10 else o1
                    val i2 = if (o2 < 10) o2 * 10 else o2
                    i1 - i2
                }.collect(Collectors.toList())

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


                val eternal = leaderboard.cutoffs[1]
                val demigod = leaderboard.cutoffs[0]


                //画上

                val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss"))
                val draw = DrawImageUtils.builder()
                draw.setTemplate(getEternalReturnDataImagePath("bg-character.jpg"))
                draw.setFont("微软雅黑", Font.BOLD)
                draw.drawString(
                    "${leaderboard.currentSeason?.currentSeason?.name ?: "正式赛季 unknown"}排名",
                    Color.WHITE,
                    40,
                    50,
                    20
                )
                draw.drawString("Based on DAK.GG Data.", Color.orange, 45, 70, 10)
                draw.drawString("最近更新: $date (30分钟后更新)", Color.gray, 40, 90, 12)
                draw.drawImage(
                    eternalReturnRequestData.checkTierIconExistThenGetPathOrDownload(
                        eternal.tierType
                    ),
                    40,
                    110,
                    30,
                    30,
                    null
                )
                draw.drawString(eternal.mmr.toString(), Color.white, 80, 130, 13)
                draw.drawImage(
                    eternalReturnRequestData.checkTierIconExistThenGetPathOrDownload(
                        demigod.tierType
                    ),
                    140,
                    110,
                    30,
                    30,
                    null
                )
                draw.drawString(demigod.mmr.toString(), Color.white, 180, 130, 13)

                val height: Int = draw.height
                val h: Int = height / 3
                var x = 400
                var num = 1
                for (i in tierTypes) {
                    if ((h * (num - 1) + 20) > height) {
                        x += 170
                        num = 1
                    }

                    draw.drawImage(
                        eternalReturnRequestData.checkTierIconExistThenGetPathOrDownload(
                            i
                        ),
                        x,
                        h * (num - 1) + 20,
                        20,
                        20,
                        null
                    )
                    draw.drawString(
                        "${count[i]}人(${doubleToPercentage(rate[i]!!, 100)}%)",
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
                return cqImg
            }
        }
        return "无法正常与dak.gg建立连接"
    }

    fun cutoffs(): String {
        return redisUtils.getCache(
            "Eternal_Return:cutoffs",
            String::class.java,
            { draw() },
            30L,
            TimeUnit.MINUTES,
            EternalReturnCutoffsDraw::class.java
        )!!
    }


}




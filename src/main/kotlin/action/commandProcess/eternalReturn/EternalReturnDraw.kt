package cn.luorenmu.action.commandProcess.eternalReturn


import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnDistributions
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
import java.util.stream.Collectors

/**
 * @author LoMu
 * Date 2024.08.03 9:26
 */
@Component
class EternalReturnDraw(
    private val redisTemplate: RedisTemplate<String, String>,
    private val eternalReturnRequestData: EternalReturnRequestData,
) {
    private fun doubleToPercentage(value: Double, i: Int): String {
        return "%.1f".format(value * i)
    }

    fun cutoffs(): String {
        val opsForValue = redisTemplate.opsForValue()
        opsForValue["Eternal_Return:cutoffs"]?.let { redisData ->
            return redisData
        }

        val tierDistributions = eternalReturnRequestData.tierDistributionsFind()
        tierDistributions?.let { td ->
            eternalReturnRequestData.leaderboardFind()?.let { leaderboard ->
                // 段位
                val tierTypes = td.distributions.stream().map { ds -> ds.tierType }.distinct().sorted { o1, o2 ->
                    var i1 = if (o1 > 10) o1 / 10 else o1
                    var i2 = if (o2 > 10) o2 / 10 else o2
                    if (i1 == i2) {
                        i1 = o1 % 10
                        i2 = o2 % 10
                    }
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
                synchronized(redisTemplate) {
                    //再次检查
                    opsForValue["Eternal_Return:cutoffs"]?.let { redisData ->
                        return redisData
                    }
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
                    var x = 500
                    var num = 1
                    for (i in tierTypes) {
                        if ((h * (num - 1) + 20) > height) {
                            x += 200
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
                    opsForValue["Eternal_Return:cutoffs", cqImg, 30L] = TimeUnit.MINUTES
                    return cqImg
                }
            }
        }
        return "无法正常与dak.gg建立连接"
    }


    fun leaderboard(rank: Int): String {
        val i = rank - 1
        val eternalReturnImagePath = getEternalReturnImagePath("leaderboard${rank}.png")

        redisTemplate.opsForValue()["Eternal_Return:leaderboard${rank}"]?.let {
            redisTemplate
        }

        eternalReturnRequestData.leaderboardFind()?.let { leaderboard ->
            val currentSeason = leaderboard.currentSeason
            val player = leaderboard.leaderboards[i]
            val builder = DrawImageUtils.builder()
            val drawImageUtils = builder.setTemplate(getEternalReturnDataImagePath("leaderboard.png"))
            val mostCharacters = player.mostCharacters
            val characterInfo1 = mostCharacters[0]
            val characterInfo2 = mostCharacters.getOrNull(1)
            val characterInfo3 = mostCharacters.getOrNull(2)
            var character = leaderboard.characterById[characterInfo1.characterId]
            drawImageUtils.setFont("思源黑体", 1)
            drawImageUtils.drawString(currentSeason?.currentSeason?.name ?: "正式赛季 unknown", Color.BLACK, 8, 35, 20)
            drawImageUtils.setFont("思源黑体", 0)
            drawImageUtils.drawString("$rank.", Color.BLACK, 34, 130, 15)
            drawImageUtils.drawImage(
                eternalReturnRequestData.checkCharacterImgExistThenGetPathOrDownload(character!!.key),
                90,
                100,
                40,
                40,
                null
            )
            drawImageUtils.drawString(player.nickname, Color.BLACK, 145, 130, 15)

            val playerTier = leaderboard.playerTierByUserNum[player.userNum.toInt()]

            drawImageUtils.drawImage(
                eternalReturnRequestData.checkTierIconExistThenGetPathOrDownload(playerTier?.tierType ?: 6),
                400,
                105,
                40,
                40,
                null
            )
            drawImageUtils.drawString(playerTier?.name ?: "灭钻", Color.black, 450, 130, 15)
            drawImageUtils.setFont("思源黑体", Font.BOLD)
            drawImageUtils.drawString(player.mmr.toString(), Color.black, 550, 130, 15)
            drawImageUtils.setFont("思源黑体", 0)
            drawImageUtils.drawString("#${doubleToPercentage(player.avgPlacement, 1)}", Color.black, 650, 130, 15)
            drawImageUtils.drawString("${doubleToPercentage(player.top3Rate, 100)}%", Color.black, 756, 130, 15)
            drawImageUtils.drawString(doubleToPercentage(player.avgPlayerKill, 1), Color.black, 855, 130, 15)
            drawImageUtils.drawImage(
                eternalReturnRequestData.checkCharacterImgExistThenGetPathOrDownload(character.key),
                964,
                100,
                40,
                40,
                null
            )
            drawImageUtils.drawString("${doubleToPercentage(characterInfo1.pickRate, 100)}%", Color.black, 964, 155, 15)

            characterInfo2?.let { c2 ->
                character = leaderboard.characterById[c2.characterId]
                drawImageUtils.drawImage(
                    eternalReturnRequestData.checkCharacterImgExistThenGetPathOrDownload(character!!.key),
                    1010,
                    100,
                    40,
                    40,
                    null
                )
                drawImageUtils.drawString("${doubleToPercentage(c2.pickRate, 100)}%", Color.black, 1010, 155, 15)


                characterInfo3?.let { c3 ->
                    character = leaderboard.characterById[c3.characterId]
                    drawImageUtils.drawImage(
                        eternalReturnRequestData.checkCharacterImgExistThenGetPathOrDownload(
                            character!!.key
                        ), 1056, 100, 40, 40, null
                    )
                    drawImageUtils.drawString(
                        "${doubleToPercentage(characterInfo3.pickRate, 100)}%",
                        Color.black,
                        1061,
                        155,
                        15
                    )
                }
            }

            drawImageUtils.drawString("喵喵喵?", Color.gray, 1000, 24, 10)
            drawImageUtils.saveImage(eternalReturnImagePath)

            val cqImg = MsgUtils.builder().img(eternalReturnImagePath).text(player.nickname).build()
            redisTemplate.opsForValue()["Eternal_Return:leaderboard${rank}", cqImg, 12L] = TimeUnit.HOURS
            return cqImg
        }
        return "无法正常与dak.gg建立连接"
    }

    fun historyProfile(nickname: String): String {
        eternalReturnRequestData.currentSeason()?.let { season ->
            eternalReturnRequestData.profile(season.currentSeason.key)?.let {
                val playerSeason = it.playerSeasons.stream().filter { playerSeasonInfo ->
                    playerSeasonInfo.mmr != null
                }.collect(Collectors.toList())

            }
        }


        return ""
    }

}




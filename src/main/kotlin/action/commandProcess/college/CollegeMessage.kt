package cn.luorenmu.action.commandProcess.college

import cn.luorenmu.common.utils.DrawImageUtils
import cn.luorenmu.common.utils.getImagePath
import cn.luorenmu.entiy.Request
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.request.RequestController
import cn.luorenmu.task.entiy.CourseDataApiResponse
import cn.luorenmu.task.entiy.RefreshTokenDataApiResponse
import cn.luorenmu.task.entiy.WorkDate
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.awt.Color
import java.awt.Font
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.text.toInt

/**
 * @author LoMu
 * Date 2024.09.30 13:37
 */
@Component
class CollegeMessage(
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val redisTemplate: StringRedisTemplate,
) {
    fun requestCourse(): CourseDataApiResponse? {
        val courseUrl = oneBotConfigRepository.findOneByConfigName("courseUrl")
        val lphyToken = oneBotConfigRepository.findOneByConfigName("LPhyToken")
        val requestController = RequestController(RequestDetailed().apply {
            url = courseUrl!!.configContent
            method = "GET"
            headers = listOf<Request.RequestParam>(Request.RequestParam().apply {
                name = "LphyToken"
                content = lphyToken!!.configContent
            })
        })
        val resp = requestController.request()
        resp?.let {
            return resp.body().to<CourseDataApiResponse>()
        }
        return null
    }

    /**
     *  返回token 当token无法查询到时返回null
     */
    fun refreshToken(): String? {
        val lPhyToken = oneBotConfigRepository.findOneByConfigName("LPhyToken")
        val response = RequestController(RequestDetailed().apply {

            url = oneBotConfigRepository.findOneByConfigName("RefreshTokenUrl")!!.configContent
            method = "POST"
            body = listOf<Request.RequestParam>(Request.RequestParam().apply {
                name = "lphytoken"
                content = lPhyToken!!.configContent
            },
                Request.RequestParam().apply {
                    name = "logincode"
                    content = oneBotConfigRepository.findOneByConfigName("logincode")?.configContent
                        ?: "0d35OO100PExQS10nj2008Are835OO1f"
                })

        }).request()
        response?.let {
            val result = response.body().to<RefreshTokenDataApiResponse>()
            if (result.success) {
                lPhyToken!!.configContent = result.data.first().lphytoken
                oneBotConfigRepository.save(lPhyToken)
                return result.data.first().lphytoken
            }
        }
        return null
    }


    fun isWorkDay(date: LocalDateTime): Boolean {
        val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        redisTemplate.opsForValue()["wordDay:$dateStr"]?.let {
            return it.to<Boolean>()
        }
        val resp = RequestController(RequestDetailed().apply {
            url = "https://date.appworlds.cn/work?date=$dateStr"
            method = "GET"
        }).request()
        resp?.let {
            val workData = it.body().to<WorkDate>()
            val work = workData.data.work
            redisTemplate.opsForValue()["wordDay:$dateStr", workData.data.work.toJSONString(), 1L] = TimeUnit.DAYS
            return work
        }
        return false
    }

    fun currentWeek(): Double {
        val configDate = oneBotConfigRepository.findOneByConfigName("startDate")!!.configContent
        val startWeek = oneBotConfigRepository.findOneByConfigName("startWeek")!!.configContent.toInt()
        val startDate = LocalDateTime.parse(configDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val nowDate = LocalDateTime.now()
        val daysBetween = ChronoUnit.DAYS.between(startDate, nowDate)
        val weeksBetween = daysBetween.toDouble() / 7
        return startWeek + weeksBetween

    }


    fun sendCourse(): String? {
        val now = LocalDateTime.now()
        var courseResp = requestCourse()
        courseResp?.let {
            // 没有正确返回 尝试刷新token
            if (!courseResp.success) {
                refreshToken()?.let {
                    courseResp = requestCourse()
                    // 刷新token依然无法处理 跳过此次
                    courseResp?.let { course ->
                        if (!course.success) {
                            return "刷新token依然无法正常处理 ${course.msg}"
                        }
                    } ?: run {
                        return "刷新token依然无法正常处理 courseResp is null"
                    }
                }
            }

            val course = courseResp!!.data
            val weekNow = LocalDateTime.now().dayOfWeek
            val courseListNow = course.filter { it.week.toInt() == weekNow.value }.sortedBy { it.lesson }
            /**
             *  替换重复内容为空
             */
            for (courseData1 in courseListNow) {
                for (courseData2 in courseListNow) {
                    if (courseData2 == courseData1) {
                        continue
                    }
                    if (courseData2.subject.contains(courseData1.subject)) {
                        courseData2.subject = courseData2.subject.replace(courseData1.subject, "")
                    }
                }
            }
            if (courseListNow.isEmpty()) {
                return "今日无课"
            }

            val currentWeek = currentWeek()
            val drawImageUtils = DrawImageUtils.builder()
            drawImageUtils.setTemplate(getImagePath("course_template"))
            drawImageUtils.setFont("微软雅黑", Font.BOLD)
            drawImageUtils.drawString("本周为第${currentWeek.toInt()}周", Color.BLACK, 500, 50, 30)
            drawImageUtils.drawString(
                "${now.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))} 星期${weekNow.value}",
                Color.BLACK,
                430,
                100,
                30
            )
            val offset = 100
            val weekRegex = "([0-9]{1,2}周)".toRegex()
            val filterRegex = "[0-9]{1,2}".toRegex()
            var i = 1
            for (courseNowDay in courseListNow) {
                var skipCurrentCourse = false
                val spiltCourse = courseNowDay.subject.split("\n").filter { it.isNotBlank() }

                for (string in spiltCourse) {
                    if (string.contains(weekRegex)) {
                        val weeks = filterRegex.findAll(string)
                        // 范围
                        if (weeks.count() == 2) {
                            val startWeek = weeks.first().value.toInt()
                            val endWeek = weeks.last().value.toInt()
                            if (!(startWeek <= currentWeek && endWeek >= currentWeek)) {
                                skipCurrentCourse = true
                            }
                        }
                        // 具体
                        else if (weeks.count() == 1) {
                            val week = weeks.first().value.toInt()
                            if (currentWeek < week) {
                                skipCurrentCourse = true
                            }
                        }
                    }
                }
                if (skipCurrentCourse) {
                    continue
                }

                drawImageUtils.drawString("第${courseNowDay.lesson}节课", Color.BLACK, 50, 50 + (i * offset), 25)
                drawImageUtils.drawString(".${courseNowDay.time}节", Color.BLACK, 145, 53 + (i * offset), 15)
                for ((j, course) in spiltCourse.withIndex()) {
                    drawImageUtils.drawString(course, Color.BLACK, (j + 1) * 200, 100 + (i * offset), 20)
                }
                i++
            }
            val path = getImagePath("course")
            drawImageUtils.saveImage(path)

            return if (i == 1) "今日无课" else MsgUtils.builder().img(path).build()
        }
        return null
    }
}
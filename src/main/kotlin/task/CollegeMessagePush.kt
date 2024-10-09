package cn.luorenmu.task

import cn.luorenmu.action.commandProcess.college.CollegeMessage
import cn.luorenmu.common.extensions.sendPrivateMsgLimit
import cn.luorenmu.repository.OneBotConfigRepository
import com.mikuac.shiro.core.BotContainer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.18 17:42
 */
@Component
class CollegeMessagePush(
    val botContainer: BotContainer,
    val collegeMessage: CollegeMessage,
    val oneBotConfigRepository: OneBotConfigRepository,
    val redisTemplate: StringRedisTemplate,
) {


    @Scheduled(cron = "0 0 7 * * ?") // 每天早上7点
    fun executeTaskAtSeven() {
        coursePush()
    }

    @Scheduled(cron = "0 30 12 * * ?") // 每天中午12点30分
    fun executeTaskAtNoon() {
        coursePush()
    }

    @Scheduled(cron = "0 0 0/1 * * ? ")
    fun timingRefreshToken() {
        collegeMessage.refreshToken() ?: run {
            sendOwnerError("刷新token失败")
        }
    }

    fun coursePush() {
        val now = LocalDateTime.now()
        if (!collegeMessage.isWorkDay(now)) {
            return
        }

        val groups = oneBotConfigRepository.findAllByConfigName("CoursePushGroup").stream()
            .map { it.configContent.toLong() }.toList()
        if (groups.isEmpty()) {
            return
        }

        val message = collegeMessage.sendCourse()

        for (group in groups) {
            getBot().sendGroupMsg(group, message,false)
        }

    }

    fun sendOwnerError(msg: String) {
        val bot = getBot()
        bot.sendPrivateMsgLimit(2842775752, msg)
    }

    fun getBot() =
        botContainer.robots.values.first()

}

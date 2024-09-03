package cn.luorenmu.action

import cn.luorenmu.common.extensions.sendGroupDeepMsgLimit
import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.OneBotConfigRepository
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import kotlin.random.Random

private val log = KotlinLogging.logger { }

@Component
open class RandomActiveSendMessage(
    val botContainer: BotContainer,
    private val oneBotConfigRespository: OneBotConfigRepository,
    private val activeSendMessageRepository: ActiveSendMessageRepository,
) {


    @Async
    open fun start() {
        val minute = 60 * 1000L // 1 minute in milliseconds
        while (true) {
            val minDelay = oneBotConfigRespository.findOneByConfigName("min_delay")!!.configContent.toLong()
            val maxDelay = oneBotConfigRespository.findOneByConfigName("max_delay")!!.configContent.toLong()

            val delayTime = Random.nextLong(minDelay * minute, maxDelay * minute)
            log.info { "Next active message will execute after ${delayTime / minute} minutes" }
            Thread.sleep(delayTime)
            executeActiveMessage()
        }

    }


     fun executeActiveMessage() {
        val firstOrNull = botContainer.robots.entries.firstOrNull()
        firstOrNull?.run {
            val groupList = value.groupList
            val activeGroupName = oneBotConfigRespository.findOneByConfigName("active_group_name")
            val groupIds = ArrayList<Long>()
            val activeMessage = activeSendMessageRepository.findAll().random()
            log.info { "行动消息 -> $activeMessage.message " }
            // 筛选合适的群
            for (datum in groupList.data) {
                activeGroupName?.run {
                    if (configContent.contains(datum.groupName)) {
                        groupIds.add(datum.groupId)
                    }
                }
            }

            // 主动发送groupId不为-1时消息 (指定群)
            activeMessage?.run {
                if (groupId != -1L) {
                    log.info { "send active message to $groupId  message: $message" }
                    value.sendGroupDeepMsgLimit(groupId, message, nextMessage)
                    return
                }
            }

            // 非指定群
            if (groupIds.isNotEmpty()) {
                val group = groupIds.random()
                activeMessage?.run {
                    log.info { "send active message to $group  message: $message" }
                    value.sendGroupDeepMsgLimit(group, message, nextMessage)
                    return
                }
            }
        }
    }

}
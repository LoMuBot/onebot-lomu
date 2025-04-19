package cn.luorenmu.action

import cn.luorenmu.common.extensions.sendGroupDeepMsgLimit
import cn.luorenmu.repository.ActiveSendMessageRepository
import cn.luorenmu.repository.OneBotConfigRepository
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.io.File
import kotlin.random.Random

private val log = KotlinLogging.logger { }

@Component
open class RandomActiveSendMessage(
    val botContainer: BotContainer,
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val activeSendMessageRepository: ActiveSendMessageRepository,
) {
    @Async
    open fun start() {
        val minute = 60 * 1000L // 60 minute
        while (true) {
            val minDelay = oneBotConfigRepository.findOneByConfigName("min_delay")!!.configContent.toLong()
            val maxDelay = oneBotConfigRepository.findOneByConfigName("max_delay")!!.configContent.toLong()

            val delayTime = Random.nextLong(minDelay * minute, maxDelay * minute)
            log.info { "Next active message will execute after ${delayTime / minute} minutes" }
            Thread.sleep(delayTime)
            executeActiveMessage()
        }

    }


    private fun executeActiveMessage() {
        val firstOrNull = botContainer.robots.entries.firstOrNull()
        val banGroup = oneBotConfigRepository.findOneByConfigName("ban_group")!!.configContent
        firstOrNull?.run {
            val groupIds = value.groupList.data.filter {
                !banGroup.contains(it.groupId.toString())
            }
            val group = groupIds.random().groupId
            if (Random.nextBoolean()) {
                val file = File("H:\\bot\\夏紫萱表情包\\夏紫萱表情包").listFiles().random()
                log.info { "行动消息夏紫萱 -> $group " }
                value.sendGroupDeepMsgLimit(group, MsgUtils.builder().img(file.absolutePath).build(), null)
                return
            }
            val activeMessage = activeSendMessageRepository.findAll().random()
            log.info { "行动消息 -> ${activeMessage.message} " }

            // 主动发送groupId不为-1时消息 (指定群)
            activeMessage?.run {
                if (groupId != -1L) {
                    log.info { "send active message to $groupId  message: $message" }
                    value.sendGroupDeepMsgLimit(groupId, message, nextMessage)
                    return
                }
            }

            // 非指定群
            activeMessage?.run {
                log.info { "send active message to $group  message: $message" }
                value.sendGroupDeepMsgLimit(group, message, nextMessage)
                return
            }
        }
    }
}

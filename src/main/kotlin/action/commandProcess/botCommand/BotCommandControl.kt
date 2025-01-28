package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.action.commandProcess.botCommand.entity.OneBotCommandConfig
import cn.luorenmu.action.commandProcess.botCommand.entity.OneBotConfigList
import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entity.OneBotConfig
import com.alibaba.fastjson2.to
import com.alibaba.fastjson2.toJSONString
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.01.28 17:52
 */
@Component
class BotCommandControl(
    private val configRepository: OneBotConfigRepository,
    private val redisUtils: RedisUtils,
) {

    /**
     * 兼容旧版
     */
    fun changeCommandState(commandName: String, sender: MessageSender): String {
        val all = redisUtils.cacheThenReturn(commandName) {
            val allConfig = configRepository.findAllByConfigName(commandName)
            OneBotConfigList(allConfig).toJSONString()
        }.to<OneBotConfigList>()

        all.list.firstOrNull { it.configContent == sender.senderName }?.let {
            it.configContent =
                OneBotCommandConfig(
                    false,
                    sender.role,
                    sender.groupOrSenderId,
                    sender.senderId,
                    LocalDateTime.now()
                ).toJSONString()
            configRepository.save(it)
            return "已关闭该功能"
        } ?: run {
            configRepository.save(
                OneBotConfig(
                    null, commandName, OneBotCommandConfig(
                        true,
                        sender.role,
                        sender.groupOrSenderId,
                        sender.senderId,
                        LocalDateTime.now()
                    ).toJSONString()
                )
            )
            return "已开启该功能"
        }
    }
}
package cn.luorenmu.action.commandProcess

import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.OneBotCommandConfigRepository
import cn.luorenmu.repository.entity.OneBotCommandConfig
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.01.28 17:52
 */
@Component
class BotCommandControl(
    private val configRepository: OneBotCommandConfigRepository,
    private val redisUtils: RedisUtils,
) {

    fun commandState(commandName: String, groupId: Long): Boolean? {
        val result = redisUtils.cacheThenReturn("${commandName}State:${groupId}") {
            configRepository.findByCommandNameAndGroupId(commandName, groupId)?.state.toString()
        }
        return result?.toBoolean()
    }

    fun changeCommandState(commandName: String, sender: MessageSender): String {
        try {
            val commandConfig = configRepository.findByCommandNameAndGroupId(commandName, sender.groupOrSenderId)
            commandConfig?.let { config ->
                if (sender.role.roleNumber >= config.role.roleNumber || sender.unlimited) {
                    config.state = !config.state
                    config.date = LocalDateTime.now()
                    config.role = sender.role
                    config.senderId = sender.senderId
                    configRepository.save(config)
                    if (config.state) {
                        return "$commandName 已启用 更改该权限至少需要\n${sender.role}"
                    }
                    return "$commandName 已禁用 更改该权限至少需要\n${sender.role}"
                } else {
                    return "你没有权限使用这个命令 因为上次更改了该功能的人权限为\n${config.role}\n" +
                            "你的权限为${sender.role}"
                }
            } ?: run {
                // 配置不存在 生成配置
                initConfig(commandName, sender, true)
                return "已为该群启用$commandName 更改该功能权限至少需要和[${sender.senderName}]同等级权限\n${sender.role}"
            }
        } finally {
            redisUtils.deleteCache("${commandName}State:${sender.groupOrSenderId}")
        }
    }

    private fun initConfig(commandName: String, sender: MessageSender, state: Boolean) {
        // 配置不存在 生成配置
        configRepository.save(
            OneBotCommandConfig(
                null, commandName, state, sender.role, sender.groupOrSenderId, sender.senderId,
                LocalDateTime.now()
            )
        )
    }
}
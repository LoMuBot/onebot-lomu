package cn.luorenmu.action.commandProcess.botCommand

import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.listen.entity.MessageSender
import cn.luorenmu.repository.OneBotCommandConfigRepository
import cn.luorenmu.repository.entity.OneBotCommandConfig
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
    private val configRepository: OneBotCommandConfigRepository,
    private val redisUtils: RedisUtils,
) {

    fun commandState(commandName: String, groupId: Long): Boolean {
        return redisUtils.cacheThenReturn("${commandName}State-${groupId}") {
            configRepository.findByCommandNameAndGroupId(commandName, groupId)?.state?.toString() ?: run {
                initConfig(commandName, groupId, false)
                false.toString()
            }
        }.toBoolean()
    }

    fun changeCommandState(commandName: String, sender: MessageSender): String {
        val commandConfig = redisUtils.cacheThenReturn(commandName) {
            configRepository.findByCommandNameAndGroupId(commandName, sender.groupOrSenderId).toJSONString()
        }.to<OneBotCommandConfig?>()

        commandConfig?.let { config ->
            if (sender.role.roleNumber >= config.role.roleNumber || sender.unlimited) {
                config.state = !config.state
                config.date = LocalDateTime.now()
                config.role = sender.role
                config.senderId = sender.senderId
                configRepository.save(config)
                return "$commandName 状态已取反-当前状态为${config.state}"
            } else {
                return "你没有权限使用这个命令 因为禁用了该功能的人权限为${config.role.role}:${config.role.roleNumber} " +
                        "你的权限为${sender.role.role}:${sender.role.roleNumber}"
            }
        } ?: run {
            // 配置不存在 生成配置
            initConfig(commandName, sender, true)
            return "已启用该功能 如需关闭再次以同样的命令At 并且权限等级需大于或等于${sender.senderId}|${sender.role.role}:${sender.role.roleNumber}"
        }
    }

    private fun initConfig(commandName: String, groupId: Long, state: Boolean) {
        // 配置不存在 生成配置
        configRepository.save(
            OneBotCommandConfig(
                null, commandName, state, BotRole.GroupAdmin, groupId, groupId,
                LocalDateTime.now()
            )
        )
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
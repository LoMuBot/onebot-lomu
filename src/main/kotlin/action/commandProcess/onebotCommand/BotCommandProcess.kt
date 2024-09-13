package cn.luorenmu.action.commandProcess.onebotCommand

import cn.luorenmu.action.PermissionsManager
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.OneBotConfig
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.08.14 20:18
 */
@Component
class BotCommandProcess(
    private val oneBotConfigRepository: OneBotConfigRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val permissionsManager: PermissionsManager,
) {


    fun banKeyword(groupId: Long, role: String, id: Long): String {
        if (permissionsManager.isAdmin(role,id)) {
            oneBotConfigRepository.save(OneBotConfig(null, "banKeywordGroup", groupId.toString()))
            redisTemplate.delete("banKeywordGroup")
            return "已屏蔽该群"
        }
        return ""
    }

    fun banStudy(groupId: Long, role: String, id: Long): String {
        if (permissionsManager.isAdmin(role,id)) {
            oneBotConfigRepository.save(OneBotConfig(null, "banStudy", groupId.toString()))
            redisTemplate.delete("banStudy")
            return "已屏蔽该群"
        }
        return ""
    }

    fun unbanKeyword(groupId: Long, role: String, id: Long): String {
        if (permissionsManager.isAdmin(role,id)) {
            for (config in oneBotConfigRepository.findAllByConfigName("banKeywordGroup")) {
                if (config.configContent.toLong() == groupId) {
                    oneBotConfigRepository.delete(config)
                    redisTemplate.delete("banKeywordGroup")
                    return "已解除对该群的屏蔽"
                }
            }
        }
        return ""
    }

    fun unbanStudy(groupId: Long, role: String, id: Long): String {
        if (permissionsManager.isAdmin(role,id)) {
            for (config in oneBotConfigRepository.findAllByConfigName("banStudyAdmin")) {
                if (config.configContent.toLong() == groupId) {
                    return ""
                }
            }
            for (config in oneBotConfigRepository.findAllByConfigName("banStudy")) {
                if (config.configContent.toLong() == groupId) {
                    oneBotConfigRepository.delete(config)
                    redisTemplate.delete("banStudy")
                    return "已解除对该群的屏蔽"
                }
            }
        }
        return ""
    }
}
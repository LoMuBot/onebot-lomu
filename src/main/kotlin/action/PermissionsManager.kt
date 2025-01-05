package cn.luorenmu.action

import cn.luorenmu.common.extensions.checkThenDelete
import cn.luorenmu.common.extensions.checkThenSave
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.OneBotConfig
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.09.10 09:00
 */
@Component
class PermissionsManager(
    private val redisTemplate: StringRedisTemplate,
    private val configRepository: OneBotConfigRepository,
) {


    private fun deleteConfigBotAdminOrGroupAdmin(
        sendId: Long,
        role: String,
        configName: String,
        groupId: Long,
    ): Boolean {
        val groupIdStr = groupId.toString()
        if (isBotAdmin(sendId)) {
            if (configRepository.checkThenDelete("${configName}Admin", groupId) && configRepository.checkThenDelete(
                    configName,
                    groupId
                )
            ) {
                return true
            }
        }
        if (isAdmin(role, groupId)) {
            configRepository.findFirstByConfigNameAndConfigContent("${configName}Admin", groupIdStr)?.let {
                return false
            }
            if (configRepository.checkThenDelete(configName, groupId)) {
                return true
            }
        }
        return false
    }

    private fun saveConfigBotAdminOrGroupAdmin(
        sendId: Long,
        role: String,
        configName: String,
        groupId: Long,
    ): Boolean {
        val groupStr = groupId.toString()
        if (isBotAdmin(sendId)) {
            configRepository.checkThenSave(OneBotConfig(null, "${configName}Admin", groupStr))
            configRepository.checkThenSave(OneBotConfig(null, configName, groupStr))
            return true
        }
        if (isGroupAdmin(role)) {
            configRepository.checkThenSave(OneBotConfig(null, configName, groupStr))
            return true
        }
        return false
    }

    private fun saveConfig(configName: String, groupId: Long) {
        configRepository.checkThenSave(OneBotConfig(null, configName, groupId.toString()))
    }


    fun isBotAdmin(id: Long): Boolean {
        val admins = configRepository.findAllByConfigName("Admin")
        if (admins.isNotEmpty()) {
            for (admin in admins) {
                if (admin.configContent.toLong() == id) {
                    return true
                }
            }
        }
        return false
    }

    fun isGroupAdmin(role: String): Boolean = role == "owner" || role == "admin"


    fun isAdmin(role: String, id: Long): Boolean {
        if (role == "owner" || role == "admin") {
            return true
        }
        val admins = configRepository.findAllByConfigName("Admin")
        if (admins.isNotEmpty()) {
            for (admin in admins) {
                if (admin.configContent.toLong() == id) {
                    return true
                }
            }
        }
        return false
    }


    fun banKeyword(groupId: Long, role: String, id: Long): String =
        if (saveConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banKeywordGroup",
                groupId
            )
        ) {
            redisTemplate.delete("banKeywordGroup")
            "已屏蔽该群"
        } else ""

    fun bilibiliEventListen(groupId: Long, role: String, id: Long): String =
        if (saveConfigBotAdminOrGroupAdmin(
                id,
                role,
                "BilibiliEventListen",
                groupId
            )
        ) {
            redisTemplate.delete("BilibiliEventListen")
            "已监听该群"
        } else ""

    fun banBilibiliEventListen(groupId: Long, role: String, id: Long): String =
        if (deleteConfigBotAdminOrGroupAdmin(
                id,
                role,
                "BilibiliEventListen",
                groupId
            )
        ) {
            redisTemplate.delete("BilibiliEventListen")
            "视频监听已被禁止"
        } else ""


    fun banStudy(groupId: Long, role: String, id: Long): String =
        if (saveConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banStudy",
                groupId
            )
        ) {
            redisTemplate.delete("banStudy")
            "已屏蔽该群"
        } else ""


    fun unbanKeyword(groupId: Long, role: String, id: Long): String =
        if (deleteConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banKeywordGroup",
                groupId
            )
        ) {
            redisTemplate.delete("banKeywordGroup")
            "已解除对该群的屏蔽"
        } else ""


    fun unbanStudy(groupId: Long, role: String, id: Long): String =
        if (deleteConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banStudy",
                groupId
            )
        ) {
            redisTemplate.delete("banStudy")
            "已解除对该群的屏蔽"
        } else ""

}
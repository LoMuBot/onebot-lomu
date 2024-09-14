package cn.luorenmu.action

import cn.luorenmu.common.extensions.checkThenDelete
import cn.luorenmu.common.extensions.checkThenSave
import cn.luorenmu.repository.OneBotConfigRepository
import cn.luorenmu.repository.entiy.OneBotConfig
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.09.10 09:00
 */
@Component
class PermissionsManager(
    private val configRepository: OneBotConfigRepository,
) {


    fun deleteConfigBotAdminOrAdmin(
        sendId: Long,
        role: String,
        botAdminConfigName: String,
        groupAdminConfigName: String,
        groupId: Long,
    ): Boolean {
        if (isBotAdmin(sendId)) {
            if (configRepository.checkThenDelete(botAdminConfigName, groupId)) {
                return true
            }
        }
        if (isAdmin(role, groupId)) {
            if (configRepository.checkThenDelete(groupAdminConfigName, groupId)) {
                return true
            }
        }
        return false
    }

    fun saveConfigBotAdminOrAdmin(
        sendId: Long,
        role: String,
        botAdminConfigName: String,
        groupAdminConfigName: String,
        groupId: Long,
    ): Boolean {
        val groupStr = groupId.toString()
        if (isBotAdmin(sendId)) {
            configRepository.checkThenSave(OneBotConfig(null, botAdminConfigName, groupStr))
            return true
        }
        if (isAdmin(role)) {
            configRepository.checkThenSave(OneBotConfig(null, groupAdminConfigName, groupStr))
            return true
        }
        return false
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

    fun isAdmin(role: String): Boolean = role == "owner" || role == "admin"


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
}
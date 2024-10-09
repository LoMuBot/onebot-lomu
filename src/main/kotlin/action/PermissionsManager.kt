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


    fun deleteConfigBotAdminOrGroupAdmin(
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

    fun saveConfigBotAdminOrGroupAdmin(
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

    fun saveConfig(configName: String, groupId: Long) {
        configRepository.checkThenSave(OneBotConfig(null,configName,groupId.toString()))
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
}
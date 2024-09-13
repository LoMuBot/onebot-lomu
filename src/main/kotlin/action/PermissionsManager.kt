package cn.luorenmu.action

import cn.luorenmu.repository.OneBotConfigRepository
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.09.10 09:00
 */
@Component
class PermissionsManager(
    private val configRepository: OneBotConfigRepository,
) {
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
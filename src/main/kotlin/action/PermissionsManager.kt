package cn.luorenmu.action

import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.listen.entity.BotRole
import cn.luorenmu.repository.OneBotConfigRepository
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.09.10 09:00
 */
@Component
class PermissionsManager(
    private val configRepository: OneBotConfigRepository,
    private val redisUtils: RedisUtils,
) {


    fun isOwner(id: Long): Boolean {
        return redisUtils.cacheThenReturn("Owner") {
            configRepository.findAllByConfigName("Owner").toString()
        }?.contains(id.toString()) ?: false

    }

    fun isAdmin(id: Long): Boolean {
        return redisUtils.cacheThenReturn("Admin") {
                configRepository.findAllByConfigName("Admin").toString()
            }?.contains(id.toString()) ?: false
    }

    fun botRole(sender: Long, role: String): BotRole {
        return when {
            isOwner(sender) -> BotRole.OWNER
            isAdmin(sender) -> BotRole.ADMIN
            else -> when (role) {
                "admin" -> BotRole.GroupAdmin
                "owner" -> BotRole.GroupOwner
                else -> BotRole.Member
            }
        }

    }
}
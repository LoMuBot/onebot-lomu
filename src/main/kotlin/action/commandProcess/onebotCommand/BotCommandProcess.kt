package cn.luorenmu.action.commandProcess.onebotCommand

import cn.luorenmu.action.PermissionsManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.08.14 20:18
 */
@Component
class BotCommandProcess(
    private val redisTemplate: RedisTemplate<String, String>,
    private val permissionsManager: PermissionsManager,
) {


    fun banKeyword(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.saveConfigBotAdminOrAdmin(
                id,
                role,
                "banKeywordGroupAdmin",
                "banKeywordGroup",
                groupId
            )
        ) {
            redisTemplate.delete("banKeywordGroup")
            "已屏蔽该群"
        } else ""


    fun banStudy(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.saveConfigBotAdminOrAdmin(
                id,
                role,
                "banStudyAdmin",
                "banStudy",
                groupId
            )
        ) {
            redisTemplate.delete("banStudy")
            "已屏蔽该群"
        } else ""


    fun unbanKeyword(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.deleteConfigBotAdminOrAdmin(
                id,
                role,
                "banKeywordGroupAdmin",
                "banKeywordGroup",
                groupId
            )
        ) {
            redisTemplate.delete("banKeywordGroup")
            "已解除对该群的屏蔽"
        } else ""


    fun unbanStudy(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.deleteConfigBotAdminOrAdmin(
                id,
                role,
                "banStudyAdmin",
                "banStudy",
                groupId
            )
        ) {
            redisTemplate.delete("banStudy")
            "已解除对该群的屏蔽"
        } else ""


}
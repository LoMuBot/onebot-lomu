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

    fun coursePush(groupId: Long, id: Long): String {
        if (permissionsManager.isBotAdmin(id)) {
            permissionsManager.saveConfig("CoursePushGroup", groupId)
            return "已为该群推送"
        }
        return ""
    }

    fun banKeyword(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.saveConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banKeywordGroup",
                groupId
            )
        ) {
            redisTemplate.delete("banKeywordGroup")
            "已屏蔽该群"
        } else ""

    fun banBilibiliEventListen(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.saveConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banBilibiliEventListen",
                groupId
            )
        ) {
            redisTemplate.delete("banBilibiliEventListen")
            "已屏蔽该群"
        } else ""

    fun unbanBilibiliEventListen(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.deleteConfigBotAdminOrGroupAdmin(
                id,
                role,
                "banBilibiliEventListen",
                groupId
            )
        ) {
            redisTemplate.delete("banBilibiliEventListen")
            "已解除对该群的屏蔽"
        } else ""


    fun banStudy(groupId: Long, role: String, id: Long): String =
        if (permissionsManager.saveConfigBotAdminOrGroupAdmin(
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
        if (permissionsManager.deleteConfigBotAdminOrGroupAdmin(
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
        if (permissionsManager.deleteConfigBotAdminOrGroupAdmin(
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
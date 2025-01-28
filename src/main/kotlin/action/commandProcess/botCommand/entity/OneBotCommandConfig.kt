package cn.luorenmu.action.commandProcess.botCommand.entity

import cn.luorenmu.listen.entity.BotRole
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.01.28 16:33
 */
data class OneBotCommandConfig(
    val state: Boolean,
    val role: BotRole,
    val groupId: Long,
    val senderId: Long,
    val date: LocalDateTime,
)
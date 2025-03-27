package cn.luorenmu.repository.entity

import cn.luorenmu.listen.entity.BotRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.01.28 16:33
 */
@Document(collection = "one_bot_command_config")
data class OneBotCommandConfig(
    @Id
    val id: String?,
    val commandName: String,
    var state: Boolean,
    var role: BotRole = BotRole.GroupAdmin,
    @Indexed
    var groupId: Long,
    var senderId: Long,
    var date: LocalDateTime = LocalDateTime.now(),
)
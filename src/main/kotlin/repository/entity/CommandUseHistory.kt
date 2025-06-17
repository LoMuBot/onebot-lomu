package cn.luorenmu.repository.entity

import cn.luorenmu.listen.entity.MessageSender
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.redis.core.index.Indexed
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2025.06.07 18:04
 */
@Document(collection = "command_use_history")
data class CommandUseHistory(
    @Id
    var id: String? = null,
    @Indexed
    val senderInfo: MessageSender,
    val commandName: String,
    val date: LocalDateTime = LocalDateTime.now(),
)

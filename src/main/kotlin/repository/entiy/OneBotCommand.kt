package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author LoMu
 * Date 2024.07.31 22:37
 */
@Document(collection = "one_bot_command")
data class OneBotCommand(
    @Id
    var id: String?,
    @Indexed
    var keyword: String,
    var commandName: String,
    var needAtMe: Boolean
    )

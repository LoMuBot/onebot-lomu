package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author LoMu
 * Date 2024.09.20 12:05
 */
@Document(collection = "messages_to_command")
data class MessageToCommand(
    @Id
    val id: String?,
    @Indexed
    val keyword: String,
    val command: String,
    // 指定发送者 当为-1 表示支持任何人
    val senderId: Long,
    // 指定群 当为-1 表示支持所有群
    val groupId: Long,

    ) {
    constructor(
        keyword: String,
        command: String,
        // 指定发送者 当为-1 表示支持任何人
        senderId: Long,
        // 指定群 当为-1 表示支持所有群
        groupId: Long,
    ) : this(null, keyword, command, senderId, groupId) {
    }
}

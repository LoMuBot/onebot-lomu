package cn.luorenmu.listen.entity

/**
 * @author LoMu
 * Date 2024.12.12 17:00
 */
data class MessageSender(
    var groupOrSenderId: Long,
    var senderName: String,
    var senderId: Long,
    var role: BotRole,
    var messageId: Int,
    var message: String,
    var messageType: MessageType,
    var botId: Long,
    // unlimited is true disregard role permissions limit
    var unlimited: Boolean = false,
)

enum class MessageType(val type: String) {
    PRIVATE("private"), GROUP("group");
    companion object {
        fun convert(type: String): MessageType {
            return MessageType.entries.first { it.type == type }
        }
    }
}

enum class BotRole(val role: String, val roleNumber: Int) {
    OWNER("bot_owner", 999),
    ADMIN("bot_admin", 10),
    GroupOwner("owner", 4),
    GroupAdmin("admin", 3),
    Member("member", 0);

    override fun toString(): String {
        return when (this) {
            OWNER -> "¿?"
            ADMIN -> "Bot管理者"
            GroupAdmin -> "群管理员"
            GroupOwner -> "群主"
            Member -> "群成员"
        }
    }


    companion object {
        fun convert(role: String): BotRole {
            return entries.first { it.role == role }
        }
    }
}
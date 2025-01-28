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
    // unlimited is true if disregard role limit
    var unlimited: Boolean = false,
)

enum class MessageType {
    PRIVATE, GROUP
}

 enum class BotRole(val role: String, val roleNumber: Int){
    OWNER("owner", 10),
    ADMIN("admin", 5),
    GroupOwner("group_owner", 4),
    GroupAdmin("group_admin", 3),
    Member("member", 0);


    companion object {
        fun convert(role: String): BotRole {
            return entries.first { it.role == role }
        }
    }
}
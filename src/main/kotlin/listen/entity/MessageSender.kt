package cn.luorenmu.listen.entity

/**
 * @author LoMu
 * Date 2024.12.12 17:00
 */
data class MessageSender(
    var groupOrSenderId: Long,
    var senderName: String,
    var senderId: Long,
    var role: String,
    var messageId: Int,
    var message: String,
    var messageType: MessageType,
    // unlimited is true if disregard role limit
    var unlimited: Boolean = false,
)

enum class MessageType {
    PRIVATE, GROUP
}
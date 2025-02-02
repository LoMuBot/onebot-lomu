package cn.luorenmu.service.entity

/**
 * @author LoMu
 * Date 2025.02.02 17:29
 */
enum class ChatRole(val role: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    override fun toString(): String {
        return role
    }


}
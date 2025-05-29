package cn.luorenmu.service.entity

/**
 * @author LoMu
 * Date 2025.05.24 14:18
 */
data class Greeting(
    val keywords: List<String>,
    val reply: String,
    val isEmpty: Boolean,
    val isActive: Boolean
)
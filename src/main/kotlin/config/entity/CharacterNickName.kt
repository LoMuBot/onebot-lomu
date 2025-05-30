package cn.luorenmu.config.entity

/**
 * @author LoMu
 * Date 2025.05.30 00:12
 */
data class CharacterNickName(
    val character: String,
    val nickName: MutableList<String> = mutableListOf()
)
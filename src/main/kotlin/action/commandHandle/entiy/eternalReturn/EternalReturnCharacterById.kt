package cn.luorenmu.action.commandHandle.entiy.eternalReturn

/**
 * @author LoMu
 * Date 2024.07.31 9:00
 */
data class EternalReturnCharacterById(
    val id: Int,
    val backgroundImageUrl: String,
    val communityImageUrl: String,
    val imgUrl: String?,
    val key: String,
    val name: String,
)

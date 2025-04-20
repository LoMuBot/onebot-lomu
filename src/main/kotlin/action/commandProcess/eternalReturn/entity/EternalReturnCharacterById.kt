package action.commandProcess.eternalReturn.entity

/**
 * @author LoMu
 * Date 2024.07.31 9:00
 */
data class EternalReturnCharacterById(
    val id: Int,
    val key: String,
    val name: String,
    val imageName: String,
    val imageUrl: String,
    val backgroundImageUrl: String,
    val communityImageUrl: String,
    val fullImageUrl: String,
    val resultImageUrl: String,
    val weaponTypes: List<WeaponType>,
    val skins: List<Skin>,
) {
    data class Skin(
        val id: Long,
        val name: String,
        val grade: Long,
        val imageName: String,
        val imageUrl: String,
        val fullImageUrl: String,
    )

    data class WeaponType(
        val id: Long,
        val key: String,
    )

    enum class CharacterImgUrlType(val type: String) {
        BackgroundImageUrl("BackgroundImage"),
        FullImageUrl("FullImage"),
        ResultImageUrl("ResultImage"),
        CommunityImageUrl("CommunityImage"),
        ImageUrl("Image"),
        CharProfileImageUrl("CharProfileImage")
    }
}

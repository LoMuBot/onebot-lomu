package action.commandProcess.eternalReturn.entity

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.08.07 8:30
 */
data class EternalReturnCharacterInfo(
    val pageProps: EternalReturnCharacterInfoPageProps,
)

data class EternalReturnCharacterInfoPageProps(
    @JSONField(name = "dehydratedState")
    val dehydratedState: EternalReturnDehydratedState,
    @JSONField(name = "randomCharacter")
    val randomCharacter: EternalReturnCharacterInfoPagePropsRandomCharacter,
)

data class EternalReturnCharacterInfoPagePropsRandomCharacter(
    @JSONField(name = "masteries")
    val masteries: List<String>,
)

data class EternalReturnDehydratedState(
    @JSONField(name = "queries")
    val queries: List<EternalReturnQueries>,
)

data class EternalReturnQueries(
    @JSONField(name = "state")
    val state: EternalReturnState,
)

data class EternalReturnState(
    @JSONField(name = "data")
    val data: EternalReturnStateData,
)

data class EternalReturnStateData(
    @JSONField(name = "weaponTypes")
    val weaponTypes: List<EternalReturnStateDataWeaponType>?,
    @JSONField(name = "weaponType")
    val weaponType: EternalReturnStateDataWeaponType?,
)

data class EternalReturnStateDataWeaponType(
    val id: Int,
    val imageUrl: String,
    val key: String,
    val name: String,
)



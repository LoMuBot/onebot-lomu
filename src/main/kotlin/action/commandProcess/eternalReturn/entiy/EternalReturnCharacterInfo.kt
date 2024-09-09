package cn.luorenmu.action.commandProcess.eternalReturn.entiy

/**
 * @author LoMu
 * Date 2024.08.07 8:30
 */
data class EternalReturnCharacterInfo(
    val pageProps : EternalReturnCharacterInfoPageProps
)

data class EternalReturnCharacterInfoPageProps(
    val randomCharacter : EternalReturnCharacterInfoPagePropsRandomCharacter
)

data class EternalReturnCharacterInfoPagePropsRandomCharacter(
    val masteries : List<String>,
)
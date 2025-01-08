package cn.luorenmu.action.commandProcess.eternalReturn.entiy

/**
 * @author LoMu
 * Date 2024.07.31 9:48
 */
data class EternalReturnCurrentSeason(
    val seasons: List<EternalReturnSeasons>
){
    val currentSeason: EternalReturnSeasons = seasons.stream().filter(EternalReturnSeasons::isCurrent).findFirst().get()
}

package action.commandProcess.eternalReturn.entity.profile

/**
 * @author LoMu
 * Date 2024.08.03 14:09
 */
/**
 *  url -> https://er.dakgg.io/api/v1/players/%EC%BB%A4%EB%A6%AC/profile?season=SEASON_13
 */
data class EternalReturnProfile (
    val meta: EternalReturnProfileMeta,
    val player: EternalReturnProfilePlayer,
    val playerSeasonOverviews: List<EternalReturnProfilePlayerSeasonOverviews>?,
    val playerSeasons : List<EternalReturnProfilePlayerSeason>
)
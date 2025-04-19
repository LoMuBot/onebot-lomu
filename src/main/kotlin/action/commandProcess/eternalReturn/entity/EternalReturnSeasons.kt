package action.commandProcess.eternalReturn.entity

/**
 * @author LoMu
 * Date 2024.08.05 7:18
 */

/**
 *     {
 *       "id": 30,
 *       "key": "PRE_SEASON_16",
 *       "name": "季前赛 7"
 *     },
 *     {
 *       "id": 31,
 *       "key": "SEASON_16",
 *       "name": "正式赛季 S7",
 *       "isCurrent": true
 *     }
 */
data class EternalReturnSeasons(
    val id: Int,
    val key: String,
    val name: String,
    val isCurrent: Boolean = false
)

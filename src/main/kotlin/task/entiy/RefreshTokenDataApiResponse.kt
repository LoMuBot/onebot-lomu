package cn.luorenmu.task.entiy

/**
 * @author LoMu
 * Date 2024.09.26 19:59
 */

data class RefreshTokenDataApiResponse(
    val success: Boolean,
    val status: Int,
    val msg: String,
    val data: List<RefreshTokenData>,
    )

data class RefreshTokenData(
    val lphytoken: String,
    val lphyuserid: String,
    val usergroup: String,
    val reauthorize: String,
    val uploadstep: String,
)
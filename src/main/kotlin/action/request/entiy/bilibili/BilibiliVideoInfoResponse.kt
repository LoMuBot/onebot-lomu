package cn.luorenmu.action.request.entiy.bilibili

/**
 * @author LoMu
 * Date 2025.02.13 05:33
 */
data class BilibiliVideoInfoResponse(
    var code: Int,
    var message: String,
    var ttl: Int,
    var data: List<BilibiliVideoInfoData>,
)

data class BilibiliVideoOwner(
    val mid: String,
    val name: String,
    val face: String,
)

data class BilibiliVideoInfoData(
    val bvid:String,
    val title: String,
    val pic: String,
    val owner: BilibiliVideoOwner,
    val desc:String
)

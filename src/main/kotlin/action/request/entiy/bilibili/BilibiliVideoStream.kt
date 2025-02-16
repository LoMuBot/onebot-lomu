package cn.luorenmu.action.request.entiy.bilibili

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.09.12 21:54
 */
data class BilibiliVideoStreamInfo(
    var code: Int,
    var message: String,
    var ttl: Int,
    var data: List<BilibiliVideoInfoStreamData>,
)

data class BilibiliVideoInfoStreamData(
    @JSONField(name = "format")
    var format: String,
    @JSONField(name = "timelength")
    val timelength: Long,
    var durl: List<BilibiliVideoInfoDataDurl>,
)

data class BilibiliVideoInfoDataDurl(
    var order: Int,
    var length: Long,
    var size: Long,
    var url: String,
)

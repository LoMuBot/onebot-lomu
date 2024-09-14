package cn.luorenmu.action.listenProcess.entiy

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.09.12 21:16
 */
data class BilibiliPageListInfo(
    var code: Int,
    var message: String,
    var ttl: Int,
    var data: List<BilibiliPageInfoData>,
)

data class BilibiliPageInfoData(
    var cid: Long,
    var page: Int,
    var from: String,
    var part: String,
    var duration: Int,
    var vid: String,
    var weblink: String,
    @JSONField(name = "first_frame")
    val firstFrame: String?,

    )
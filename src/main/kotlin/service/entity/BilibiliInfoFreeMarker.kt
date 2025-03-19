package cn.luorenmu.service.entity

import com.alibaba.fastjson2.annotation.JSONField
import kotlin.reflect.KClass

/**
 * @author LoMu
 * Date 2025.03.18 20:38
 */
data class BilibiliInfoFreeMarker(
    @JSONField(name = "video_bg")
    val videoBg: String,
    val avatar: String,
    val port: String,
    @JSONField(name = "up_name")
    val upName: String,
    val title: String,
    val desc: String,
)
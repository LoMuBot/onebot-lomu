package cn.luorenmu.config.shiro.customAction.response

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2025.02.22 14:38
 */
data class GetImageResponse(
    val file: String,
    val url: String,
    @JSONField(name = "file_size")
    val fileSize: String,
    @JSONField(name = "file_name")
    val fileName: String,
)
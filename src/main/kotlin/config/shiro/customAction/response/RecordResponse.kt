package cn.luorenmu.config.shiro.customAction.response

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.09.19 21:52
 */
data class RecordResponse(
    val file: String,
    val url: String,
    @JSONField(name = "file_size")
    val fileSize: String,
    @JSONField(name = "file_name")
    val fileName: String,
)

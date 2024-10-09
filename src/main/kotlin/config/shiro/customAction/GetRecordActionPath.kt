package cn.luorenmu.config.shiro.customAction

import com.mikuac.shiro.enums.ActionPath

/**
 * @author LoMu
 * Date 2024.09.19 12:42
 */
enum class GetRecordActionPath(private val path: String ): ActionPath {
    GetRecord("get_record");

    override fun getPath(): String = path
}
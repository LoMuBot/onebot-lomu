package cn.luorenmu.config.shiro.customAction

import com.mikuac.shiro.enums.ActionPath

/**
 * @author LoMu
 * Date 2025.02.13 14:54
 */
enum class GetMessageActionPath(private val path: String) : ActionPath {
    GetMsg("get_msg");
    override fun getPath(): String = path
}
package cn.luorenmu.config.shiro.customAction

import com.mikuac.shiro.enums.ActionPath

/**
 * @author LoMu
 * Date 2025.02.13 14:48
 */
enum class GetGroupMsgHistoryActionPath(private val path: String) : ActionPath {
    GetGroupMsgHistory("get_group_msg_history");

    override fun getPath(): String = path
}
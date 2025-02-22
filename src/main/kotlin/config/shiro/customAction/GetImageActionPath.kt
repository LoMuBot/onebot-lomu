package cn.luorenmu.config.shiro.customAction

import com.mikuac.shiro.enums.ActionPath

/**
 * @author LoMu
 * Date 2025.02.22 14:36
 */
enum class GetImageActionPath(private val path: String ): ActionPath {
    GetImage("get_image");

    override fun getPath(): String = path
}
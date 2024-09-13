package cn.luorenmu.config.shiro.customAction

import com.mikuac.shiro.enums.ActionPath

/**
 * @author LoMu
 * Date 2024.09.10 04:46
 */
enum class SetEmojiLikeActionPath(private val path: String) : ActionPath {

    SetMsgEmojiLike("set_msg_emoji_like");

    override fun getPath(): String = path
}
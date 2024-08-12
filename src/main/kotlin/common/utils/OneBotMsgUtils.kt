package cn.luorenmu.common.utils

import com.mikuac.shiro.common.utils.MsgUtils

/**
 * @author LoMu
 * Date 2024.08.02 14:59
 */


fun isImage(msg: String): Boolean {
    return msg.startsWith("[CQ:image") && msg.endsWith("]")
}

fun getCQFileStr(msg: String): String? {
    if (msg.startsWith("[CQ:image") && msg.endsWith("]")) {
        val regex = """file=([^,]+)""".toRegex()
        val matchResult = regex.find(msg)
        if (matchResult != null) {
            return matchResult.groupValues[1]
        }
    }
    return null
}

fun replaceCqToFileStr(msg: String): String? {
    if (isImage(msg)) {
        return getCQFileStr(msg)
    }
    return null
}

fun isCQAt(msg: String): Boolean {
    return msg.contains("[CQ:at")
}

fun isCQReply(msg: String): Boolean {
    return msg.contains("[CQ:reply")
}
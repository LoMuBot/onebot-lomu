package cn.luorenmu.common.extensions

import com.github.promeg.pinyinhelper.Pinyin
import com.mikuac.shiro.common.utils.MsgUtils

/**
 * @author LoMu
 * Date 2024.09.05 14:12
 */
fun String.firstPinYin(): String {
    val string = StringBuilder()
    for (i in this.indices) {
        string.append(Pinyin.toPinyin(this[i]).first())
    }
    return string.toString()
}

fun String.replaceAtToEmpty(id: Long): String {
    return this.replace(MsgUtils.builder().at(id).build(), "")
}

fun String.replaceBlankToEmpty(): String {
    return this.uppercase().replace(" ", "")
}


fun String.toPinYin(): String {
    val string = StringBuilder()
    for (i in this.indices) {
        string.append(Pinyin.toPinyin(this[i]))
    }
    return string.toString()
}

fun String.isImage(): Boolean {
    return this.contains("[CQ:image")
}


// QQ表情包
fun String.isMface(): Boolean {
    return this.startsWith("[CQ:mface") && this.endsWith("]")
}

fun String.getCQFileStr(): String? {
    if (isImage()) {
        val regex = """file=([^,]+)""".toRegex()
        val matchResult = regex.find(this)
        if (matchResult != null) {
            return matchResult.groupValues[1]
        }
    }
    return null
}

fun String.replaceCqToFileStr(): String? {
    if (this.isImage()) {
        return this.getCQFileStr()
    }
    return null
}

fun String.isCQAt(): Boolean {
    return this.contains("[CQ:at")
}

fun String.isAt(id: Long): Boolean {
    return this.contains(MsgUtils.builder().at(id).build())
}

fun String.isCQReply(): Boolean {
    return this.contains("[CQ:reply")
}


fun String.isCQStr(): Boolean {
    return this.contains("[CQ:")
}

fun String.isCQJson(): Boolean {
    return this.contains("[CQ:json")
}

fun String.isCQRecord(): Boolean {
    return this.contains("[CQ:record")
}
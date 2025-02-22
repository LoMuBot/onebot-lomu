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

fun String.replaceAtToEmpty(): String {
    return this.replace("\\[CQ:at,qq=(\\d+)]".toRegex(), "")
}

fun String.getAtQQ(i: Int = 0): String? {
    return "\\[CQ:at,qq=(\\d+)?+]".toRegex().findAll(this).toMutableList().getOrNull(i)?.groupValues?.get(1)
}

fun String.replaceBlankToEmpty(): String {
    return this.replace(" ", "")
}

fun String.replaceReplyToEmpty(): String {
    return this.replace("\\[CQ:reply,id=(\\d+)]".toRegex(), "")
}

fun String.replaceImageToEmpty(): String {
    return this.replace("\\[CQ:image,.*?]".toRegex(), "")
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

fun String.getCQReplyMessageId(): String? {
    if (this.isCQReply()) {
        return "\\[CQ:reply,id=(\\d+)?+]".toRegex().find(this)?.groups?.get(1)?.value
    }
    return null
}

fun String.getCQUrlStr(index: Int = 0): String? {
    if (isImage()) {
        val regex = """url=([^,]+)""".toRegex()
        val matchResult = regex.findAll(this).toList()
        if (matchResult.isNotEmpty()) {
            return matchResult.getOrNull(index)?.groupValues?.get(1)
        }
    }
    return null
}

fun String.getCQFileStr(index: Int = 0): String? {
    val regex = """file=([^,]+)""".toRegex()
    val matchResult = regex.findAll(this).toList()
    if (matchResult.isNotEmpty()) {
        return matchResult.getOrNull(index)?.groupValues?.get(1)
    }
    return null
}

fun String.getFileStr(index: Int = 0): String? {
    val regex = """"file"\s*:\s*"([^"]+)"""".toRegex()
    val matchResult = regex.findAll(this).toList()
    if (matchResult.isNotEmpty()) {
        return matchResult.getOrNull(index)?.groupValues?.get(1)
    }
    return null
}

fun String.replaceImgCqToFileStr(): String {
    if (this.isImage()) {
        return this.getCQFileStr() ?: this
    }
    return this
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

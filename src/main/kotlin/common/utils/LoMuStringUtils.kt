package cn.luorenmu.common.utils

import com.github.promeg.pinyinhelper.Pinyin


/**
 * @author LoMu
 * Date 2024.08.07 9:17
 */

fun firstPinYin(chinese: String):String {
    val string = StringBuilder()
    for (i in chinese.indices) {
        string.append(Pinyin.toPinyin(chinese[i]).first())
    }
    return string.toString()
}
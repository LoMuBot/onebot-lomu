package cn.luorenmu

import com.mikuac.shiro.common.utils.MsgUtils

/**
 * @author LoMu
 * Date 2024.07.04 23:55
 */


fun main() {
    val str = "月这把发挥优点不好 你下吧要再这样我就把你的妈妈藏起来让你找不到"
    val regex = Regex("(((小月)|(月神)|(徐陆)|(冬月阑珊))(.*?)(妈妈))")
    println(str.contains(regex))
    println(MsgUtils.builder().at(656668299).build())
}

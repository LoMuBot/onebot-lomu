package cn.luorenmu

/**
 * @author LoMu
 * Date 2024.07.04 23:55
 */


fun main() {
    val str = "说话"
    val regex = Regex("^(说)")
    println(str.contains(regex))

}

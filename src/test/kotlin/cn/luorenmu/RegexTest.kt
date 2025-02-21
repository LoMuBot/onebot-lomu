package cn.luorenmu

/**
 * @author LoMu
 * Date 2025.02.20 18:03
 */
fun main() {
    val regex = "https://playeternalreturn.com/posts/news/([0-9]{4,6})".toRegex()
    println(regex.find("https://playeternalreturn.com/posts/news/2536")!!.groups[1]?.value)
    println("https://playeternalreturn.com/posts/news/2536".contains(regex))
}
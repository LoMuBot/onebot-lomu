package cn.luorenmu

import cn.luorenmu.common.utils.WkhtmltoimageUtils

/**
 * @author LoMu
 * Date 2025.02.23 20:54
 */
fun main() {
   WkhtmltoimageUtils.convertUrlToImage(
       "http://127.0.0.1:5500/example/bilibili-video-info/test.html",
       "C:\\Users\\luore\\OneDrive\\图片\\test.png",
       mapOf("zoom" to "2")
       )
}
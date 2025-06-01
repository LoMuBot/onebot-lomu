package cn.luorenmu.common.extensions

/**
 * @author LoMu
 * Date 2025.05.30 20:27
 */
fun List<*>.getUrlIfIndexExists(index: Int, getUrl: (Int) -> String): String {
    return if (this.size > index) getUrl(index) else ""
}

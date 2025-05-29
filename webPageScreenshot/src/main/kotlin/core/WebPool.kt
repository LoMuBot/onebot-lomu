package cn.luorenmu.core

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author LoMu
 * Date 2025.05.29 12:04
 */
class WebPool(size: Int, headless: Boolean = true) {
    private val webPageScreenshots = run {
        val item = CopyOnWriteArrayList<WebPageScreenshot>()
        for (i in 1..size) {
            item.add(WebPageScreenshot(headless))
        }
        item
    }
    val index = AtomicInteger(0)

    fun getWebPageScreenshot(): WebPageScreenshot {
        val idx =  index.getAndUpdate { (it + 1) %  webPageScreenshots.size }
        return webPageScreenshots[idx]
    }

    fun shutdown() {
        webPageScreenshots.forEach {
            it.shutdown()
        }
    }
}
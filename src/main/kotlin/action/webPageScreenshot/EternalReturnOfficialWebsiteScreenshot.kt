package cn.luorenmu.action.webPageScreenshot

import cn.luorenmu.pool.WebPageScreenshotPool
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.20 13:55
 */
@Component
class EternalReturnOfficialWebsiteScreenshot(
    private val webPageScreenshot: WebPageScreenshotPool,
) {
    companion object {
        private const val WEBSITE_URL = "https://playeternalreturn.com/posts/news/"
    }

    /**
     * 单任务线程 底层已经保证了其线程安全 避免重复截取
     * @param id 公告id
     */
    @Synchronized
    fun screenshotNews(id: String, outputPath: String): String {
        webPageScreenshot.execute {
            it.setHttpUrl(WEBSITE_URL + id).screenshotAllCrop(400, 140, -850, -1300, 50) { webDriver ->
                // 在页面打开后删除头导航元素 因为它会跟随页面滑动 导致截图結果不符合預期
                val gnbElement: WebElement = webDriver.findElement(By.id("gnb"))
                val js = webDriver as JavascriptExecutor
                js.executeScript("arguments[0].remove();", gnbElement)
            }.outputImageFile(outputPath)
        }.get()
        return outputPath
    }
}
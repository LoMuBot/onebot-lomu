package cn.luorenmu

import cn.luorenmu.file.InitializeFile
import cn.luorenmu.web.WebPageScreenshot
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor


/**
 * @author LoMu
 * Date 2025.02.20 14:02
 */
fun main() {
    WebDriverManager.chromedriver().browserVersion("133.0.6943.99").setup()
    InitializeFile.run(MainApplication::class.java)

    val webPageScreenshot = WebPageScreenshot()
    webPageScreenshot.setHttpUrl("https://playeternalreturn.com/posts/news/2536")
    val gnbElement = webPageScreenshot.driver.findElement(By.id("gnb"))
    val js = webPageScreenshot.driver as JavascriptExecutor
    js.executeScript("arguments[0].remove();", gnbElement)
    webPageScreenshot.screenshotAllCrop(450, 140, -450, -500, 50)
        .outputImageFile("C:\\Users\\luore\\Pictures\\test.png")

    webPageScreenshot.dispose()
}
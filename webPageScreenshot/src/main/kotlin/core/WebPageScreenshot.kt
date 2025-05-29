package cn.luorenmu.core

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.BoundingBox
import com.microsoft.playwright.options.WaitUntilState
import java.nio.file.Paths

/**
 * @author LoMu
 * Date 2025.05.24 12:20
 */
class WebPageScreenshot(headless: Boolean = true) {
    private val playwright: Playwright = Playwright.create()
    private val browser: Browser =
        playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(headless))


    private val context = browser.newContext(
        Browser.NewContextOptions()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .setExtraHTTPHeaders(
                mapOf(
                    "Accept-Language" to "zh-CN,zh;q=0.9",
                    "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
                )
            )
    )

    private val page = context.newPage()
    fun customizeSelector(
        url: String,
        selector: String,
        waitUntilState: WaitUntilState = WaitUntilState.DOMCONTENTLOADED,
        pageConsumer: (page: Page, box: BoundingBox) -> Unit,
    ) {
        synchronized(this) {
            page.navigate(url, Page.NavigateOptions().setWaitUntil(waitUntilState).setTimeout(15000.0))
            val locator = page.locator(selector)
            val boundingBox = locator.boundingBox()
            pageConsumer(page, boundingBox)
        }
    }

    fun screenshotSelector(
        url: String,
        output: String,
        selector: String,
        waitUntilState: WaitUntilState = WaitUntilState.DOMCONTENTLOADED,
        pageConsumer: (page: Page) -> Unit = {},
    ) {
        synchronized(this) {
            page.navigate(url, Page.NavigateOptions().setWaitUntil(waitUntilState).setTimeout(15000.0))
            val locator = page.locator(selector)
            val boundingBox = locator.boundingBox()
            pageConsumer(page)
            page.screenshot(
                Page.ScreenshotOptions().setPath(Paths.get(output))
                    .setFullPage(true)
                    .setClip(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)
            )
        }
    }


    fun screenshot(
        url: String,
        output: String,
        waitUntilState: WaitUntilState = WaitUntilState.DOMCONTENTLOADED,
        pageConsumer: (page: Page) -> Unit = {},
    ) {
        synchronized(this) {
            page.navigate(url, Page.NavigateOptions().setWaitUntil(waitUntilState).setTimeout(15000.0))
            pageConsumer(page)
            page.screenshot(
                Page.ScreenshotOptions().setPath(Paths.get(output))
                    .setFullPage(true)
            )
        }
    }

    fun shutdown() {
        browser.close()
    }

}
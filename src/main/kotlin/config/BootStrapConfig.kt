package cn.luorenmu.config

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.MainApplication
import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.config.external.LoMuBotProperties
import cn.luorenmu.file.InitializeFile
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.pool.WebPageScreenshotPool
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.to
import io.github.bonigarcia.wdm.WebDriverManager
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

/**
 * @author LoMu
 * Date 2024.07.25 2:16
 */

private val log = KotlinLogging.logger {}

@Configuration
class BootStrapConfig(
    private val properties: LoMuBotProperties,
) {
    init {
        InitializeFile.run(MainApplication::class.java)
        ReadWriteFile.createCurrentDirs("image/qq/avatar")
        ReadWriteFile.createCurrentDirs("image/qq/deer")
        ReadWriteFile.createCurrentDirs("image/qq/petpet")
        ReadWriteFile.createCurrentDirs("qrcode/")
        ReadWriteFile.createCurrentDirs("request/")
        ReadWriteFile.createCurrentDirs("crx/")

        ReadWriteFile.readDirJsonToRunStore("keyword")

        val resourcesRequestJson = mapOf(
            "bilibili_request" to "request/bilibili_request.json",
            "eternal_return_request" to "request/eternal_return_request.json",
        )

        // 生成配置文件
        for (filePath in resourcesRequestJson) {
            var initJsonObj =
                ResourceUtil.getResource(filePath.value).openStream().bufferedReader().readText().to<JSONObject>()
            val file = File(ReadWriteFile.CURRENT_PATH + filePath.value)
            if (file.exists()) {
                var fix = false
                val jsonObj = ReadWriteFile.readCurrentFileJson(filePath.value).to<JSONObject>()
                for (mutableEntry in initJsonObj) {
                    if (!jsonObj.containsKey(mutableEntry.key)) {
                        log.warn { "${filePath.value}-> ${mutableEntry.key} not exists!  try fix" }
                        fix = true
                        jsonObj[mutableEntry.key] = mutableEntry.value
                        log.info { "fix success" }
                    }
                }
                if (fix) {
                    file.delete()
                    ReadWriteFile.entityWriteFileToCurrentDir(filePath.value, jsonObj)
                }

            } else {
                ReadWriteFile.entityWriteFileToCurrentDir(filePath.value, initJsonObj)
                initJsonObj = ReadWriteFile.readCurrentFileJson(filePath.value).to<JSONObject>()
            }

            JsonObjectUtils.putRequestJson(filePath.key, initJsonObj)
        }
    }


    @Bean(destroyMethod = "shutdown")
    fun getWebPageScreenshotPool(): WebPageScreenshotPool {
        WebDriverManager.chromedriver().browserVersion("133.0.6943.126").setup();
        return WebPageScreenshotPool(properties.webPool.size) {
            it.addArguments("--headless");
            it.addArguments("--window-size=1920,1080");
        }
    }
}
package cn.luorenmu.config

import cn.luorenmu.MainApplication
import cn.luorenmu.file.InitializeFile
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.pool.WebPageScreenshotPool
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author LoMu
 * Date 2024.07.25 2:16
 */

@Configuration
class MultiFunctionConfig {
    init {
        InitializeFile.run(MainApplication::class.java)
        ReadWriteFile.createCurrentDirs("image/")
        ReadWriteFile.createCurrentDirs("qrcode/")
        ReadWriteFile.createCurrentDirs("request/")
        ReadWriteFile.readDirJsonToRunStore("keyword")
    }


    @Bean
    fun getWebPageScreenshotPool(): WebPageScreenshotPool {
        return WebPageScreenshotPool(3);
    }
}
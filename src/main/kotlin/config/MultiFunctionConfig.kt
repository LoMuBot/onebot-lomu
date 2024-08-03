package cn.luorenmu.config

import cn.luorenmu.MainApplication
import cn.luorenmu.file.InitializeFile
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.web.WebPageScreenshot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author LoMu
 * Date 2024.07.25 2:16
 */

@Configuration
class MultiFunctionConfig {
    init {
        InitializeFile.run(MainApplication::class.java, false)
        ReadWriteFile.createCurrentDirs("image/")
        ReadWriteFile.createCurrentDirs("qrcode/")
        ReadWriteFile.createCurrentDirs("request/")
        ReadWriteFile.createCurrentDirs("image/eternal_return/nickname/")
        ReadWriteFile.createCurrentDirs("image/eternal_return/data/")
        ReadWriteFile.readDirJsonToRunStore("keyword")
    }


    @Bean
    fun getWebPageScreenshot(): WebPageScreenshot {
        return WebPageScreenshot();
    }
}
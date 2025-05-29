package cn.luorenmu

import cn.luorenmu.action.commandProcess.eternalReturn.EternalReturnFindPlayer
import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.action.webPageScreenshot.EternalReturnWebPageScreenshot
import cn.luorenmu.common.utils.FreeMarkerUtils
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.WkhtmltoimageUtils
import cn.luorenmu.core.WebPageScreenshot
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.service.LLMGenerateService
import com.mikuac.shiro.core.BotContainer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream

/**
 * @author LoMu
 * Date 2024.12.13 00:14
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Test(
    @Autowired val qqRequestData: QQRequestData,
    @Autowired val botContainer: BotContainer,
    @Autowired val lLMGenerateService: LLMGenerateService,
    @Autowired private val webPageScreenshot: EternalReturnWebPageScreenshot,
) {






    fun test() {
        println(lLMGenerateService.replyFitKeyword(mutableListOf("我要成为相机高手","哈哈"),"先成为人类吧"))
    }
}

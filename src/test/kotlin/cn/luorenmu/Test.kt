package cn.luorenmu

import cn.luorenmu.action.commandProcess.eternalReturn.EternalReturnFindPlayer
import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.common.utils.FreeMarkerUtils
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.WkhtmltoimageUtils
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
    @Autowired val eternalReturnFindPlayer: EternalReturnFindPlayer,
) {


    fun test() {
        runBlocking {
            val nowTime = System.currentTimeMillis()
            val nickname = "Stormchaser"
            val imgPath = PathUtils.getEternalReturnNicknameImagePath(nickname)
            val htmlPath = PathUtils.getEternalReturnDataImagePath("$nickname.html")
            val pageRender = runBlocking { eternalReturnFindPlayer.pageRender(nickname) }
            val parseData = FreeMarkerUtils.parseData("eternal_return_player.ftlh", pageRender)
            ReadWriteFile.writeStreamFile(htmlPath, ByteArrayInputStream(parseData.toByteArray()))

            WkhtmltoimageUtils.convertHtmlToImage(
                htmlPath, imgPath, mapOf("zoom" to "2")
            )
            val endTime = System.currentTimeMillis()
            print(endTime - nowTime)
        }
    }
}

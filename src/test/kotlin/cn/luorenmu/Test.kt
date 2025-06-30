package cn.luorenmu

import cn.luorenmu.action.commandProcess.eternalReturn.EternalReturnFindCharacter
import cn.luorenmu.action.render.EternalReturnFindPlayerRender
import cn.luorenmu.action.request.EternalReturnRequestData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author LoMu
 * Date 2024.12.13 00:14
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Test(
    @Autowired val eternalReturnRender: EternalReturnFindPlayerRender,
    @Autowired val f: EternalReturnFindCharacter,
    @Autowired val eternalReturnRequestData: EternalReturnRequestData,
) {


    fun test() {
        eternalReturnRender.imageRenderGenerate("boongwa")
    }
}

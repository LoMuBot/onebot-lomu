package cn.luorenmu.action.petpet

import cn.luorenmu.file.ReadWriteFile
import moe.dituon.petpet.core.context.RequestContext
import moe.dituon.petpet.core.element.PetpetTemplateModel
import moe.dituon.petpet.template.PetpetTemplate
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

/**
 * @author LoMu
 * Date 2025.02.05 11:30
 */
@Component
class PetpetGenerate {
    fun generate(template: PetpetTemplate, to: String, from: String, toText: String = ""): String? {
        val model = PetpetTemplateModel(template)
        val resultImage = model.draw(
            RequestContext( // 传入图像数据
                mapOf(
                    "to" to to,
                    "from" to from
                ),
                mapOf(
                    "to" to toText
                )
            )
        )
        val localPath = "${ReadWriteFile.CURRENT_PATH.substring(1)}/image/qq/petpet"
        val savePath = "$localPath/${UUID.randomUUID()}.${resultImage.format}"
        resultImage.save(savePath)
        return savePath
    }


}
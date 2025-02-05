package cn.luorenmu.action.petpet

import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.file.ReadWriteFile
import moe.dituon.petpet.core.context.RequestContext
import moe.dituon.petpet.core.element.PetpetTemplateModel
import moe.dituon.petpet.template.PetpetTemplate
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.05 11:30
 */
@Component
class PetpetGenerate(
    private val qqRequestData: QQRequestData,
) {
    fun generate(template: PetpetTemplate, to: String, from: String): String? {
        val toQQAvatar: String
        val fromQQAvatar: String
        try {
            toQQAvatar = qqRequestData.downloadQQAvatar(to)
            fromQQAvatar = qqRequestData.downloadQQAvatar(from)
        } catch (e: IllegalStateException) {
            return e.message
        }
        val model = PetpetTemplateModel(template)
        val resultImage = model.draw(
            RequestContext( // 传入图像数据
                mapOf(
                    "to" to toQQAvatar,
                    "from" to fromQQAvatar
                ),
                mapOf(
                    "text1" to "L"
                )
            )
        )
        val savePath = "${ReadWriteFile.CURRENT_PATH.substring(1)}/image/qq/${model.metadata.alias.first()}-$to-$from.${resultImage.format}"
        resultImage.save(savePath)


        return savePath
    }
}
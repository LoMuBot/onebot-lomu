package cn.luorenmu.action.listenProcess

import cn.luorenmu.action.petpet.PetpetGenerate
import cn.luorenmu.action.petpet.TemplateRegister
import cn.luorenmu.common.extensions.getAtQQ
import cn.luorenmu.common.extensions.isAt
import cn.luorenmu.common.extensions.replaceAtToBlank
import cn.luorenmu.common.extensions.replaceBlankToEmpty
import cn.luorenmu.listen.entity.MessageSender
import com.mikuac.shiro.common.utils.MsgUtils
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.02.05 11:27
 */
@Component
class PetpetListen(
    private val petpetGenerate: PetpetGenerate,
) {

    fun process(messageSender: MessageSender): String? {
        if (!messageSender.message.isAt(messageSender.botId)) {
            val replaceAt = messageSender.message.replaceAtToBlank().replaceBlankToEmpty()
            TemplateRegister.getTemplate(replaceAt)?.let {
                val path =
                    petpetGenerate.generate(
                        it,
                        messageSender.message.getAtQQ() ?: messageSender.senderId.toString(),
                        messageSender.senderId.toString()
                    )
                return MsgUtils.builder().img(path).build()
            }
        }
        return null
    }
}
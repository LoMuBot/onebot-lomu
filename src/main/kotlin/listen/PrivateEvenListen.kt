package cn.luorenmu.listen

import cn.luorenmu.commom.extensions.sendPrivateMsgLimit
import com.mikuac.shiro.annotation.PrivateMessageHandler
import com.mikuac.shiro.annotation.PrivateMsgDeleteNoticeHandler
import com.mikuac.shiro.annotation.common.Shiro
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import com.mikuac.shiro.dto.event.notice.PrivateMsgDeleteNoticeEvent
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2024.07.05 7:58
 */

@Component
@Shiro
class PrivateEvenListen {

    @PrivateMessageHandler
    fun privateMessageHandler(bot: Bot, privateMessage: PrivateMessageEvent) {
        bot.sendPrivateMsgLimit(privateMessage.userId, "private message do not handle, don't send private message")
    }

    @PrivateMsgDeleteNoticeHandler
    fun privateDeleteHandler(bot: Bot, privateMessage: PrivateMsgDeleteNoticeEvent) {

    }
}
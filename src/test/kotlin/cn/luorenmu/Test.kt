package cn.luorenmu

import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.sendPrivateMsg
import cn.luorenmu.service.ChatService
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author LoMu
 * Date 2024.12.13 00:14
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class Test(
    @Autowired val qqRequestData: QQRequestData,
    @Autowired val botContainer: BotContainer,
    @Autowired val chatService: ChatService
) {

    @Test
    fun testQQAvatar() {
        chatService.extractKeywordsFromReply(mutableListOf("好下头","没有童年的吗","你有赛车驾照吗","你吃屎了吗","我操你妈","傻逼"), "你才傻逼")
    }
}

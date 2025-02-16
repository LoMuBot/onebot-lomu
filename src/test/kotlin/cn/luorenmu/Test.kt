package cn.luorenmu

import cn.luorenmu.action.request.QQRequestData
import cn.luorenmu.common.extensions.getFirstBot
import cn.luorenmu.common.extensions.sendPrivateMsg
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.BotContainer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author LoMu
 * Date 2024.12.13 00:14
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Test(
    @Autowired val qqRequestData: QQRequestData,
    @Autowired val botContainer: BotContainer,
) {

    fun testQQAvatar() {
        botContainer.getFirstBot().sendPrivateMsg(
            2842775752L,
            MsgUtils.builder().img(qqRequestData.downloadQQAvatar(3141298408.toString()).substring(1)).build())
    }
}

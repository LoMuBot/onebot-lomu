package cn.luorenmu.action.listenProcess

import cn.luorenmu.common.extensions.isCQRecord
import cn.luorenmu.config.shiro.customAction.getRecord
import cn.luorenmu.repository.RecordMessageRepository
import cn.luorenmu.repository.entiy.RecordMessage
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.19 12:10
 *
 * 娱乐功能
 *
 */

@Component
class GroupSpecialListen(
    private val recordMessageRepository: RecordMessageRepository,
) {

    /**
     * 存储需要修改群名片的群
     *  key group id
     *  value bot card
     */
    var currentBotCard = hashMapOf<Long, String>()

    /**
     *  从消息中监听群名片(群备注) 并实时跟踪
     */
    fun userNameListen(bot: Bot, group: GroupMessageEvent) {
        val groupId = group.groupId
        val card = group.sender.card

        currentBotCard[groupId]?.let {
            if (card != it) {
                bot.setGroupCard(group.groupId, bot.selfId, card)
            }
        }

    }

    fun recordMessageListen(bot: Bot, group: GroupMessageEvent) {
        if (group.message.isCQRecord()) {
            // 语音文件只允许作为单独的消息发送 所以只存在一个
            val recordMsg = group.arrayMsg.first()
            val recordFileName = recordMsg.data["file"]
            val resp = bot.getRecord(recordFileName!!, "wav")
            if (resp.status == "ok") {
                val path = resp.data.file
                recordMessageRepository.save(
                    RecordMessage(
                        null,
                        group.sender.userId,
                        group.groupId,
                        path,
                        LocalDateTime.now()
                    )
                )
            }
        }
    }
}
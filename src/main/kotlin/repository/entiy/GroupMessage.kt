package cn.luorenmu.repository.entiy


import com.mikuac.shiro.dto.event.message.GroupMessageEvent

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.07.04 10:48
 */
@Document("group_message")
data class GroupMessage(
    @Id
    var id: String?,
    @Indexed
    var groupId: Long,
    var botId: Long,
    var sendDate: LocalDateTime,
    var groupEventObject: GroupMessageEvent
) {




}
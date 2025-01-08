package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.09.03 14:59
 */
@Document(collection = "overdue_keyword")
data class OverdueKeyword (
    @Id
    var id: String?,
    var senderId: Long,
    @Indexed
    var keyword: String,
    var reply: String,
    var needProcess: Boolean,
    var atMe: Boolean,
    var groupId: Long,
    var createdDate: LocalDateTime,
    var overdueDate: LocalDateTime,
    val triggers: Int?,
    var nextMessage: DeepMessage?,
){
    constructor(keyword: KeywordReply) :
            this(keyword.id,keyword.senderId,keyword.keyword,keyword.reply,keyword.needProcess,keyword.atMe,keyword.groupId!!,keyword.createdDate!!,LocalDateTime.now(),keyword.triggers,keyword.nextMessage)
}
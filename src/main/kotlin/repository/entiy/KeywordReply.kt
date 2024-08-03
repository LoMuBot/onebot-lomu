package cn.luorenmu.repository.entiy

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author LoMu
 * Date 2024.07.26 23:24
 */
@Document("keyword_reply")
data class KeywordReply(
    @Id
    var id: String?,
    var senderId: Long,
    @Indexed
    var keyword: String,
    var reply: String,
    var needProcess: Boolean,
    var atMe: Boolean,
    var nextMessage: DeepMessage?,
) {
    fun deepMessage(list: ArrayList<String>, nextMessage: DeepMessage?): ArrayList<String> {
        nextMessage?.let {
            list.add(it.reply)
            deepMessage(list, it.next)
        }
        return list
    }
}


package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.KeywordReply
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDateTime

/**
 * @author LoMu
 * Date 2024.07.27 2:30
 */
interface KeywordReplyRepository : MongoRepository<KeywordReply, String> {
    @Query(
        """ 
              {
                "${'$'}and": [
                    {"senderId": ?0 },
                    {"keyword": {'${'$'}regex': ?1,'${'$'}options':'i' }}
                ]
            }
        """
    )
    fun findBySenderIdAndMessageRegex(senderId: Long, message: String): List<KeywordReply>

    fun findBySenderIdAndAtMe(senderId: Long, atMe: Boolean): List<KeywordReply>
    fun findBySenderIdAndAtMeAndNeedProcess(
        senderId: Long,
        atMe: Boolean,
        needProcess: Boolean,
    ): ArrayList<KeywordReply>

    fun findBySenderIdAndAtMeAndKeyword(senderId: Long, atMe: Boolean, keyword: String): ArrayList<KeywordReply>
    fun findByKeywordIsAndReplyIs(keyword: String, reply: String): KeywordReply?
    fun findBySenderId(senderId: Long): List<KeywordReply>


    fun findByCreatedDateBefore(time: LocalDateTime): List<KeywordReply>
    fun findByCreatedDateAfterAndTriggersIsNullOrTriggersIs(date: LocalDateTime, triggers: Int): List<KeywordReply>

    @Query(
        """
            {
                "keyword": { '${'$'}regex': ?0 , '${'$'}options': 'i' }, 
            }
            """
    )
    fun findByMessageRegex(message: String): List<KeywordReply>


}
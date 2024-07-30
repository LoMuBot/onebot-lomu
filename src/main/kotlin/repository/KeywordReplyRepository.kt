package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.KeywordReply
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

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

    fun findBySenderId(senderId: Long): List<KeywordReply>

    @Query(
        """
            {
                "keyword": { '${'$'}regex': ?0 , '${'$'}options': 'i' }, 
            }
            """
    )
    fun findByMessageRegex(message: String): List<KeywordReply>


}
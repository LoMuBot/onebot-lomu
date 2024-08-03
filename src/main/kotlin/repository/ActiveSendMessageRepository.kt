package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.ActiveMessage
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.07.30 2:59
 */
interface ActiveSendMessageRepository : MongoRepository<ActiveMessage, String> {
    fun findByMessageIs(message: String): ActiveMessage?
}
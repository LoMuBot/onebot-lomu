package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.EternalReturnPush
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.09.01 15:30
 */
interface EternalReturnPushRepository : MongoRepository<EternalReturnPush, String> {
    fun findBySendIsTrue(): List<EternalReturnPush>
    fun findByEmail(email: String): EternalReturnPush?
}
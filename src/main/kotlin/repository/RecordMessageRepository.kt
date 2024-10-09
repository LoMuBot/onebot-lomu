package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.RecordMessage
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.09.19 21:58
 */
interface RecordMessageRepository : MongoRepository<RecordMessage, String> {

}
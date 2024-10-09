package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.MessageToCommand
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.09.20 12:08
 */
interface MessageToCommandRepository : MongoRepository<MessageToCommand, String> {
}
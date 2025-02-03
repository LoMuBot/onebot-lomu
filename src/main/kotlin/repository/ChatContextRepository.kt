package cn.luorenmu.repository

import cn.luorenmu.repository.entity.ChatContext
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2025.02.02 23:47
 */
interface ChatContextRepository : MongoRepository<ChatContext, String> {
}
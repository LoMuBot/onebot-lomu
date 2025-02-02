package cn.luorenmu.repository

import cn.luorenmu.repository.entity.DeepSeekChatHistory
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2025.02.02 01:58
 */
interface DeepSeekChatHistoryRepository : MongoRepository<DeepSeekChatHistory, String> {
}
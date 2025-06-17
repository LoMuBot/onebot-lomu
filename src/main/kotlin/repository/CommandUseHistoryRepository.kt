package cn.luorenmu.repository

import cn.luorenmu.repository.entity.CommandUseHistory
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2025.06.07 18:06
 */
interface CommandUseHistoryRepository : MongoRepository<CommandUseHistory, String> {
}
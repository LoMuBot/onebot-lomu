package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.OneBotConfig
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.07.30 3:41
 */
interface OneBotConfigRepository : MongoRepository<OneBotConfig, String> {
    fun findOneByConfigName(id: String): OneBotConfig?
    fun findAllByConfigName(id: String): ArrayList<OneBotConfig>
}
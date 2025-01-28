package cn.luorenmu.repository

import cn.luorenmu.repository.entity.OneBotCommandConfig
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2025.01.28 19:05
 */
interface OneBotCommandConfigRepository : MongoRepository<OneBotCommandConfig, String> {
    fun findByCommandNameAndGroupId(commandName: String, groupId: Long): OneBotCommandConfig?
}
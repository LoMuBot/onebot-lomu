package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.OneBotCommand
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.07.31 22:37
 */
interface OneBotCommandRespository : MongoRepository<OneBotCommand, String> {
    fun findByCommandNameIs(name: String): ArrayList<OneBotCommand>
}
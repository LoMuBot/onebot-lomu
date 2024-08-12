package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.AtStudyMessage
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.08.11 23:52
 */
interface AtStudyRepository : MongoRepository<AtStudyMessage, String> {
}
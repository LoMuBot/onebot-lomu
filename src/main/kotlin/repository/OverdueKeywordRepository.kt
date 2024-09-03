package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.OverdueKeyword
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.09.03 15:00
 */
interface OverdueKeywordRepository : MongoRepository<OverdueKeyword, String> {
}
package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.Deer
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2025.01.07 04:50
 */
interface DeerRepository : MongoRepository<Deer, String> {
    fun findBySenderIdAndYearAndMonth(id: Long, year: Int, month: Int): Deer?
    fun findByYearAndMonth(year: Int, month: Int): List<Deer>
}
package cn.luorenmu.repository

import cn.luorenmu.repository.entiy.BilibiliVideo
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author LoMu
 * Date 2024.10.07 20:19
 */
interface BilibiliVideoRepository : MongoRepository<BilibiliVideo, String> {
    fun findFirstBybvid(bvid: String): BilibiliVideo?
}
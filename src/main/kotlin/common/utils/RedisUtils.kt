package cn.luorenmu.common.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author LoMu
 * Date 2025.01.28 16:53
 */
@Component
class RedisUtils(
    private val redisTemplate: StringRedisTemplate,
) {
    fun cacheThenReturn(key: String, find: () -> String, timeout: Long = 1L, unit: TimeUnit = TimeUnit.DAYS): String {
        redisTemplate.opsForValue()[key]?.let {
            return it
        } ?: run {
            synchronized(RedisUtils::class.java) {
                if (redisTemplate.opsForValue()[key] == null) {
                    redisTemplate.opsForValue()[key, find(), timeout] = unit
                    return find()
                } else {
                    return redisTemplate.opsForValue()[key]!!
                }
            }
        }
    }


    fun cacheThenReturn(key: String, find: () -> String?): String? {
        redisTemplate.opsForValue()[key]?.let {
            return it
        } ?: run {
            val resp = find() ?: return null
            synchronized(RedisUtils::class.java) {
                if (redisTemplate.opsForValue()[key] == null) {
                    redisTemplate.opsForValue()[key, resp, 1L] = TimeUnit.DAYS
                    return find()
                } else {
                    return redisTemplate.opsForValue()[key]
                }
            }
        }
    }

}
package cn.luorenmu.common.utils

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.toJSONString
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.StringUtils
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
    private val log = KotlinLogging.logger {}


    fun deleteCache(key: String) {
        redisTemplate.delete(key)
    }


    /**
     * 当缓存不存在时，则 set
     *
     * @param key 缓存的 key
     * @param value 被缓存的值
     * @param timeout 过期时间
     * @param timeUnit 过期时间的单位
     * @return 是否 set
     */
    fun <T> setCacheIfAbsent(key: String, value: T, timeout: Long = 1L, timeUnit: TimeUnit = TimeUnit.DAYS): Boolean {
        val json = value.toJSONString() ?: return false
        return redisTemplate.opsForValue().setIfAbsent(key, json, timeout, timeUnit) ?: false
    }

    fun <T> setCache(key: String, value: T, timeout: Long = 1L, unit: TimeUnit = TimeUnit.DAYS) {
        val json = value.toJSONString() ?: return
        redisTemplate.opsForValue()[key, json, timeout] = unit
    }

    fun <T> getCache(
        key: String,
        valueType: Class<T>,
        onMiss: (() -> T)? = null,
        timeout: Long = 1L,
        timeUnit: TimeUnit = TimeUnit.DAYS,
        lock: Class<*> = RedisUtils::class.java,
    ): T? {
        try {
            var json = redisTemplate.opsForValue()[key]
            if (StringUtils.isEmpty(json)) {
                if (onMiss == null) {
                    return null
                }
                // 上锁
                synchronized(lock::class) {
                    // 再次查询缓存，目的是判断是否前面的线程已经set过了
                    json = redisTemplate.opsForValue()[key]
                    // 第二次校验缓存是否存在
                    if (StringUtils.isEmpty(json)) {
                        val result = onMiss()
                        // 数据库中不存在
                        setCache(key, result, timeout, timeUnit)
                        return result
                    }
                }
            }

            return JSON.parseObject(json, valueType)
        } catch (e: Exception) {
            log.error(e) { e.printStackTrace() }
            return null
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
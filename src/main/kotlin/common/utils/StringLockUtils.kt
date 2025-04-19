package cn.luorenmu.common.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * @author LoMu
 * Date 2025.04.16 23:25
 */
object StringLockUtils {
    private val mutexes = ConcurrentHashMap<String, Mutex>()


    /**
     * 字符串锁
     * @param key 锁的key
     * @param block 代码块
     */
    suspend fun <T> lock(key: String, block: suspend () -> T): T {
        val mutex = mutexes.compute(key) { _, v -> v ?: Mutex() }!!

        try {
            return mutex.withLock {
                block()
            }
        } finally {
            mutexes.remove(key, mutex)
        }
    }
}
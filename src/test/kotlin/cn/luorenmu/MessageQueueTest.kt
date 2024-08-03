package cn.luorenmu

import cn.luorenmu.dto.RecentlyMessageQueue

/**
 * @author LoMu
 * Date 2024.07.30 2:40
 */
fun main() {
    val recentlyMessageQueue = RecentlyMessageQueue<Int>()
    for (i in 0..3) {
        recentlyMessageQueue.addMessageToQueue(1L,i)
    }
    println(recentlyMessageQueue.map[1L]?.size)
    println(recentlyMessageQueue.lastMessages(1L,5))
}
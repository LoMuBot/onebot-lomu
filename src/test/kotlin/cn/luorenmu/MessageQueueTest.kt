package cn.luorenmu

import cn.luorenmu.dto.RecentlyMessageQueue

/**
 * @author LoMu
 * Date 2024.07.30 2:40
 */
fun main() {
    val recentlyMessageQueue = RecentlyMessageQueue<Int>()
    for (i in 0..100) {
        recentlyMessageQueue.addMessageToQueue(1L,i)
    }
    println(recentlyMessageQueue.lastMessage(1L))
}
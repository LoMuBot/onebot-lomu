package cn.luorenmu

import cn.luorenmu.entiy.RecentlyMessageQueue

/**
 * @author LoMu
 * Date 2024.07.30 2:40
 */
fun main() {
    val recentlyMessageQueue = RecentlyMessageQueue<Int>()
    recentlyMessageQueue.addMessageToQueue(1L,0)
//    for (i in 0..100) {
//        recentlyMessageQueue.addMessageToQueue(1L,i)
//    }
    val lastMessages = recentlyMessageQueue.lastMessages(1L, 2)
    println(lastMessages.joinToString { it.toString() })


}
package cn.luorenmu

import cn.luorenmu.dto.RecentlyMessageQueue

/**
 * @author LoMu
 * Date 2024.07.30 2:40
 */
fun main() {
    val recentlyMessageQueue = RecentlyMessageQueue<Int>()
    recentlyMessageQueue.addMessageToQueue(1L,0)
    for (i in 0..100) {
        recentlyMessageQueue.addMessageToQueue(1L,i)
    }
    var key = ""
    val lastMessages = recentlyMessageQueue.lastMessages(1L, 3)
    val i = lastMessages.size - 1
    for (lastMessage in lastMessages) {
        if (lastMessage != 100) {
            key = lastMessage.toString()
            break
        }
    }
    println(i)
    println(key)

}
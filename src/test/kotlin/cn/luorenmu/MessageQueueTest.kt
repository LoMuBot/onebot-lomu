package cn.luorenmu

import cn.luorenmu.dto.RecentlyMessageQueue

/**
 * @author LoMu
 * Date 2024.07.30 2:40
 */
fun main() {
    val recentlyMessageQueue = RecentlyMessageQueue<Int>()
    recentlyMessageQueue.addMessageToQueue(1L,0)
    for (i in 0..1) {
        recentlyMessageQueue.addMessageToQueue(1L,1)
    }
    var key = ""
    val lastMessages = recentlyMessageQueue.lastMessages(1L, 5)
    var i = lastMessages.size - 1
    for (lastMessage in lastMessages) {
        if (lastMessage != 1) {
            key = lastMessage.toString()
        }
    }
    println(i)
    println(key)

}
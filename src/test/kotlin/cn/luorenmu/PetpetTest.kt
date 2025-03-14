package cn.luorenmu

import cn.luorenmu.action.petpet.TemplateRegister
import cn.luorenmu.entiy.RecentlyMessageQueue
import cn.luorenmu.file.InitializeFile
import moe.dituon.petpet.template.PetpetTemplate

/**
 * @author LoMu
 * Date 2025.02.23 20:54
 */
fun main() {
    val recentlyMessageQueue = RecentlyMessageQueue<Int>(20)
    for (i in 0 until 77) {
        recentlyMessageQueue.addMessageToQueue(1, i)
    }
    println(recentlyMessageQueue.lastMessages(1, 3))
}
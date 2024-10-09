package cn.luorenmu

import cn.luorenmu.action.commandProcess.eternalReturn.entiy.EternalReturnCharacterInfo
import cn.luorenmu.entiy.Request
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to

/**
 * @author LoMu
 * Date 2024.07.30 2:40
 */
fun main() {
//    val recentlyMessageQueue = RecentlyMessageQueue<Int>()
//    recentlyMessageQueue.addMessageToQueue(1L,0)
////    for (i in 0..100) {
////        recentlyMessageQueue.addMessageToQueue(1L,i)
////    }
//    val lastMessages = recentlyMessageQueue.lastMessages(1L, 2)
//    println(lastMessages.joinToString { it.toString() })
    val resp = RequestController(Request.RequestDetailed().apply {
        url =
            "https://dak.gg/er/_next/data/DQSE4P3zwz5YlLFX1CmCh/characters/Aya.json?teamMode=SQUAD&weaponType=Pistol&period=3day&tier=diamond_plus&character=Aya&hl=zh_CN"
        method = "GET"
    }).request()
    val result = resp.body().to<EternalReturnCharacterInfo>()
    println(result.pageProps.dehydratedState.queries.filter { it.state.data.weaponType != null }
        .first().state.data.weaponType?.name)


}
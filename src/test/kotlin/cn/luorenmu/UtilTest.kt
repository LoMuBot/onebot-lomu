package cn.luorenmu

import cn.luorenmu.file.InitializeFile
import cn.luorenmu.request.RequestController

/**
 * @author LoMu
 * Date 2025.04.14 20:51
 */
fun main() {
    InitializeFile.run(MainApplication::class.java)
    val requestProfile = RequestController("eternal_return_request.profile")
    requestProfile.replaceUrl("season", "SEASON_1")
    requestProfile.replaceUrl("name", "lomu")

}
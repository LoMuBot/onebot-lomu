package cn.luorenmu

import action.commandProcess.eternalReturn.entity.EternalReturnCharacter
import cn.luorenmu.action.commandProcess.eternalReturn.entity.item.EternalReturnItemInfos
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.request.RequestController
import com.alibaba.fastjson2.to
import java.io.FileOutputStream

/**
 * @author LoMu
 * Date 2025.04.14 20:51
 */
fun main() {
    val requestController1 = RequestController(
        RequestDetailed().apply {
            url = "https://er.dakgg.io/api/v1/data/items?hl=zh-cn"
            method = "GET"
        }
    )
    val requestController2 = RequestController(
        RequestDetailed().apply {
            url = "https://er.dakgg.io/api/v1/data/items?hl=en"
            method = "GET"
        }
    )
    val outputStream = FileOutputStream("item.txt")
    val resp1 = requestController1.request()
    val resp2 = requestController2.request()
    val eternalReturnCharacter1 = resp1.body().to<EternalReturnItemInfos>()
    val eternalReturnCharacter2 = resp2.body().to<EternalReturnItemInfos>()

    for ((index, item) in eternalReturnCharacter1.items.withIndex()) {
        outputStream.write("${item.id}:${item.name}:${eternalReturnCharacter2.items[index].name}\n".toByteArray())
        outputStream.flush()
    }
    outputStream.close()
}

fun character(){
    val requestDetailed = RequestDetailed().apply {
        url = "https://er.dakgg.io/api/v1/data/characters?hl=zh_CN"
        method = "GET"
    }
    val outputStream = FileOutputStream("character.txt")

    val resp = RequestController(requestDetailed).request()
    val eternalReturnCharacter = resp.body().to<EternalReturnCharacter>()
    for (character in eternalReturnCharacter.characters) {
        outputStream.write("${character.key}:${character.name}\n".toByteArray())
        outputStream.flush()
    }

    outputStream.close()
}
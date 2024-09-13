package cn.luorenmu.common.utils

import cn.luorenmu.file.ReadWriteFile

/**
 * @author LoMu
 * Date 2024.08.03 9:34
 */
fun getImagePath(name: String): String = ReadWriteFile.currentPathFileName("image/${name}.png").substring(1)
fun getVideoPath(name: String): String = ReadWriteFile.currentPathFileName("video/${name}").substring(1)


fun getEternalReturnNicknameImagePath(name: String): String {
    ReadWriteFile.createCurrentDirs("image/eternal_return/nickname/${name}.png")
    return ReadWriteFile.currentPathFileName("image/eternal_return/nickname/${name}.png").substring(1)
}

fun dakggCdnUrl(url: String): String = MatcherData.replaceDollardName(
    JsonObjectUtils.getString("request.eternal_return_request.dakgg_cdn"),
    "url",
    url
)

fun getEternalReturnImagePath(name: String): String {
    ReadWriteFile.createCurrentDirs("image/eternal_return/${name}")
    return ReadWriteFile.currentPathFileName("image/eternal_return/${name}").substring(1)
}

fun getEternalReturnDataImagePath(name: String): String {
    ReadWriteFile.createCurrentDirs("image/eternal_return/data/${name}")
    return ReadWriteFile.currentPathFileName("image/eternal_return/data/${name}").substring(1)
}
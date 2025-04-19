package cn.luorenmu.common.utils

import cn.luorenmu.file.ReadWriteFile
import java.io.File

/**
 * @author LoMu
 * Date 2024.08.03 9:34
 */
object PathUtils {
    private fun lastDir(path: String) {
        val lastIndexOf = path.lastIndexOf("/")
        File(path.substring(0, lastIndexOf)).mkdirs()
    }

    fun getImagePath(name: String): String {
        val path = ReadWriteFile.currentPathFileName("image/${name}.png").substring(1)
        lastDir(path)
        return path
    }

    fun getVideoPath(name: String): String = ReadWriteFile.currentPathFileName("video/${name}").substring(1)


    fun getEternalReturnNicknameImagePath(name: String): String {
        ReadWriteFile.createCurrentDirs("image/eternal_return/nickname/${name}.jpg")
        return ReadWriteFile.currentPathFileName("image/eternal_return/nickname/${name}.jpg").substring(1)
    }


    fun getEternalReturnImagePath(name: String): String {
        ReadWriteFile.createCurrentDirs("image/eternal_return/${name}")
        return ReadWriteFile.currentPathFileName("image/eternal_return/${name}").substring(1)
    }

    fun getEternalReturnDataImagePath(name: String): String {
        ReadWriteFile.createCurrentDirs("image/eternal_return/data/${name}")
        return ReadWriteFile.currentPathFileName("image/eternal_return/data/${name}").substring(1)
    }
}

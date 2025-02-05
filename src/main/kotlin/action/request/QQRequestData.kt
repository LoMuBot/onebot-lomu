package cn.luorenmu.action.request

import cn.luorenmu.common.utils.RedisUtils
import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


/**
 * @author LoMu
 * Date 2025.01.07 04:27
 */
@Component
class QQRequestData(
    private val redisUtils: RedisUtils,
) {
    /**
     * 下载qq头像
     */
    fun downloadQQAvatar(qq: String): String {
        redisUtils.getCache("qqAvatar:$qq", String::class.java, null, 1L, TimeUnit.DAYS)?.let {
            return it
        }
        val avatarPath = ReadWriteFile.currentPathFileName("image/qq/avatar/${qq}.png")
        val requestDetailed = RequestDetailed().apply {
            url = "https://q1.qlogo.cn/g?b=qq&nk=$qq&s=640"
            method = "GET"
        }
        try {
            val requestController = RequestController(requestDetailed)
            val resp = requestController.request()
            ReadWriteFile.writeStreamFile(avatarPath, resp.bodyStream())
        } catch (e: Exception) {
            try {
                requestDetailed.url = "https://q1.qlogo.cn/g?b=qq&nk=$qq&s=100"
                val requestController = RequestController(requestDetailed)
                val resp = requestController.request()
                ReadWriteFile.writeStreamFile(avatarPath, resp.bodyStream())
            } catch (e: Exception) {
                throw IllegalStateException("获取$qq 头像失败")
            }
        }
        redisUtils.setCacheIfAbsent("qqAvatar:$qq", avatarPath)
        return avatarPath
    }
}
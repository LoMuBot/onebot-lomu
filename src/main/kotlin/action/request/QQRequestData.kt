package cn.luorenmu.action.request

import cn.luorenmu.entiy.Request.RequestDetailed
import cn.luorenmu.file.ReadWriteFile
import cn.luorenmu.request.RequestController
import org.springframework.stereotype.Component

/**
 * @author LoMu
 * Date 2025.01.07 04:27
 */
@Component
class QQRequestData {
    /**
     * 下载qq头像
     */
    fun downloadQQAvatar(qq: String, path: String) {
        val requestDetailed = RequestDetailed().apply {
            url = "https://q1.qlogo.cn/g?b=qq&nk=$qq&s=100"
            method = "GET"
        }
        val requestController = RequestController(requestDetailed)
        val resp = requestController.request()
        ReadWriteFile.writeStreamFile(path, resp.bodyStream())
    }
}
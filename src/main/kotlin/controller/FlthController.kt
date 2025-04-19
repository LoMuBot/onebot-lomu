package cn.luorenmu.controller

import cn.luorenmu.common.utils.RedisUtils
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author LoMu
 * Date 2025.03.18 21:03
 */
@Controller
class FlthController(
    private val redisUtils: RedisUtils,
) {

    @GetMapping("/ftlh/{id}")
    @ResponseBody
    fun getFtlh(@PathVariable id: String, httpResponse: HttpServletResponse): String {
        return redisUtils.getCache("ftlh:$id", String::class.java)
            ?: run {
                httpResponse.status = 404
                "404"
            }
    }


}
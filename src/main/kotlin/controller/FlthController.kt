package cn.luorenmu.controller

import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.common.utils.RedisUtils
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File
import java.io.FileInputStream

/**
 * @author LoMu
 * Date 2025.03.18 21:03
 */
@Controller
class FlthController(
    private val resourceLoader: ResourceLoader,
    private val redisUtils: RedisUtils,
) {

    @GetMapping("/ftlh/{id}")
    @ResponseBody
    fun getFtlh(@PathVariable id: String, httpResponse: HttpServletResponse): String {
        return redisUtils.getCache("ftlh:$id", String::class.java) ?: run {
            httpResponse.status = 404
            "404"
        }
    }

    @GetMapping("/images/{filename}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val resource = resourceLoader.getResource("classpath:static/images/$filename")
        return ResponseEntity.ok(resource)
    }

    @GetMapping("/local_images/{dir}/{filename}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getLocalImage(@PathVariable dir: String, @PathVariable filename: String): ResponseEntity<InputStreamResource> {
        println(PathUtils.getImagePath("$dir/$filename"))
        return ResponseEntity.ok(InputStreamResource(FileInputStream(PathUtils.getImagePath("$dir/$filename"))))
    }
}
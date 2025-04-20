package cn.luorenmu.controller

import action.commandProcess.eternalReturn.entity.EternalReturnCharacterById
import cn.luorenmu.common.utils.PathUtils
import cn.luorenmu.service.ImageService
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.io.FileInputStream
import java.io.FileReader

/**
 * @author LoMu
 * Date 2025.04.16 23:12
 */
@Controller
class ImageController(
    private val resourceLoader: ResourceLoader,
    private val imageService: ImageService,
) {

    @GetMapping("/images/{filename}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val resource = resourceLoader.getResource("classpath:static/images/$filename")
        return ResponseEntity.ok(resource)
    }



    @GetMapping("/local_images/{dir}/{filename}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getLocalImage(@PathVariable dir: String, @PathVariable filename: String): ResponseEntity<InputStreamResource> {
        return ResponseEntity.ok(InputStreamResource(FileInputStream(PathUtils.getImagePath("$dir/$filename"))))
    }

    @GetMapping("/images/eternal_return/character/{type}/{id}/{skin}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getEternalReturnCharacterImage(
        @PathVariable type: EternalReturnCharacterById.CharacterImgUrlType,
        @PathVariable id: Int,
        @PathVariable skin: Long,
    ): ResponseEntity<InputStreamResource> {
        val path = imageService.getEternalReturnCharacterImage(type, id, skin)
        return ResponseEntity.ok(InputStreamResource(FileInputStream(path)))
    }
    @GetMapping("/images/eternal_return/tier/{id}")
    fun getEternalReturnTierImage(@PathVariable id: Int): ResponseEntity<InputStreamResource> {
        val path = imageService.getTierImage(id)
        return ResponseEntity.ok(InputStreamResource(FileInputStream(path)))
    }

    @GetMapping("/images/eternal_return/item/{id}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getEternalReturnItemImage(
        @PathVariable id: Long,
    ): ResponseEntity<InputStreamResource> {
        val path = imageService.getEternalReturnItemImage(id)
        return ResponseEntity.ok(InputStreamResource(FileInputStream(path)))
    }

    @GetMapping("/images/eternal_return/tactical_skill/{id}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getEternalReturnTacticalSkillImage(
        @PathVariable id: Long,
    ): ResponseEntity<InputStreamResource> {
        val path = imageService.getEternalReturnTacticalSkillImage(id)
        return ResponseEntity.ok(InputStreamResource(FileInputStream(path)))
    }

    @GetMapping("/images/eternal_return/weapon/{id}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getEternalReturnWeaponImage(
        @PathVariable id: Int,
    ): ResponseEntity<InputStreamResource> {
        val path = imageService.getEternalReturnWeaponImage(id)
        return ResponseEntity.ok(InputStreamResource(FileInputStream(path)))
    }


    @GetMapping("/images/eternal_return/item_bg/{id}", produces = ["image/svg+xml"])
    fun getEternalReturnItemBgImage(
        @PathVariable id: Int,
    ): ResponseEntity<String> {
        val path = imageService.getEternalReturnItemBgImage(id)
        return ResponseEntity.ok(FileReader(path).readText())
    }

    @GetMapping("/images/eternal_return/trait_skill/{id}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getEternalReturnTraitSkillImage(
        @PathVariable id: Long,
        @RequestParam `is`: Boolean,
    ): ResponseEntity<InputStreamResource> {
        val path = imageService.getEternalReturnTraitSkillImage(id)
        if (`is`) {
            return ResponseEntity.ok(InputStreamResource(FileInputStream(path.skillGroup)))
        }
        return ResponseEntity.ok(InputStreamResource(FileInputStream(path.skill)))
    }
}
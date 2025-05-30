package cn.luorenmu.config

import cn.hutool.core.io.resource.ResourceUtil
import cn.luorenmu.MainApplication
import cn.luorenmu.common.utils.JsonObjectUtils
import cn.luorenmu.config.entity.CharacterNickName
import cn.luorenmu.config.entity.CharacterNickNameList
import cn.luorenmu.core.WebPool
import cn.luorenmu.file.InitializeFile
import cn.luorenmu.file.ReadWriteFile
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.to
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.File

/**
 * @author LoMu
 * Date 2024.07.25 2:16
 */

private val log = KotlinLogging.logger {}

@Configuration
class BootStrapConfig {
    init {
        InitializeFile.run(MainApplication::class.java)
        ReadWriteFile.createCurrentDirs("image/qq/avatar")
        ReadWriteFile.createCurrentDirs("image/qq/deer")
        ReadWriteFile.createCurrentDirs("image/qq/petpet")
        ReadWriteFile.createCurrentDirs("request/")
        val resourcesRequestJson = mapOf(
            "bilibili_request" to "static/request/bilibili_request.json",
            "eternal_return_request" to "static/request/eternal_return_request.json",
        )

        // 生成配置文件
        for (filePath in resourcesRequestJson) {
            var initJsonObj =
                ResourceUtil.getResource(filePath.value).openStream().bufferedReader().readText().to<JSONObject>()
            val file = File(ReadWriteFile.CURRENT_PATH + filePath.value)
            if (file.exists()) {
                var fix = false
                val jsonObj = ReadWriteFile.readCurrentFileJson(filePath.value).to<JSONObject>()
                for (mutableEntry in initJsonObj) {
                    if (!jsonObj.containsKey(mutableEntry.key)) {
                        log.warn { "${filePath.value}-> ${mutableEntry.key} not exists!  try fix" }
                        fix = true
                        jsonObj[mutableEntry.key] = mutableEntry.value
                        log.info { "fix success" }
                    }
                }
                if (fix) {
                    file.delete()
                    ReadWriteFile.entityWriteFileToCurrentDir(filePath.value, jsonObj)
                }

            } else {
                ReadWriteFile.entityWriteFileToCurrentDir(filePath.value, initJsonObj)
                initJsonObj = ReadWriteFile.readCurrentFileJson(filePath.value).to<JSONObject>()
            }

            JsonObjectUtils.putRequestJson(filePath.key, initJsonObj)
        }
    }

    @Bean(destroyMethod = "shutdown")
    fun getWebPageScreenshotPool(): WebPool {
        return WebPool(5, true)
    }

    @Bean
    fun getCharacterNickName(): CharacterNickNameList {
        val classPathResource = ClassPathResource("static/character.txt")
        val characterNickNames = mutableListOf<CharacterNickName>()
        classPathResource.inputStream.bufferedReader().forEachLine {
            if (it.isNotBlank()) {
                val list = it.split(":").toMutableList()
                list.removeIf { l -> l.isBlank() }
                val first = list.removeFirst()
                characterNickNames.add(CharacterNickName(first, list))
            }
        }
        log.info { characterNickNames }
        return CharacterNickNameList(characterNickNames)
    }

}


package cn.luorenmu.action.petpet

import cn.luorenmu.file.ReadWriteFile
import io.github.oshai.kotlinlogging.KotlinLogging
import moe.dituon.petpet.old_template.OldPetpetTemplate
import moe.dituon.petpet.template.PetpetTemplate
import java.io.File

/**
 * @author LoMu
 * Date 2025.02.04 20:39
 */
class TemplateRegister {
    private val log = KotlinLogging.logger {}
    companion object {
        val petpetTemplates: HashMap<String, PetpetTemplate> by lazy { TemplateRegister().register() }
        fun getTemplate(id: String): PetpetTemplate? {
            return petpetTemplates[id] ?: run { return null }
        }
    }

    fun register(): HashMap<String, PetpetTemplate> {
        val jsonAll = readDirsAllJson(ReadWriteFile.currentDirs("templates"))
        val petpetTemplates = hashMapOf<String, PetpetTemplate>()
        jsonAll.forEach {
            val petpetTemplate = if (it.name.contains("template")) {
                PetpetTemplate.fromJsonFile(it)
            } else {
                OldPetpetTemplate.fromJsonFile(it).toTemplate()
            }

            for (petpetName in petpetTemplate.metadata.alias) {
                petpetTemplates[petpetName] = petpetTemplate
            }
        }
        log.info { "已加载模版${petpetTemplates.keys.joinToString("|") { it }}" }
        return petpetTemplates
    }

    private fun readDirsAllJson(
        files: Array<File>,
        jsonFiles: MutableList<File> = mutableListOf(),
    ): MutableList<File> {
        for (file in files) {
            if (file.isDirectory) {
                readDirsAllJson(File(file.path).listFiles()!!, jsonFiles)
            }
            if (file.name.endsWith("data.json")) {
                jsonFiles.add(file)
            }
            if (file.name.endsWith("template.json")) {
                jsonFiles.add(file)
            }
        }
        return jsonFiles
    }

}


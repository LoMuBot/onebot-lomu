package cn.luorenmu.action.petpet

import cn.luorenmu.file.ReadWriteFile
import moe.dituon.petpet.old_template.OldPetpetTemplate
import moe.dituon.petpet.template.PetpetTemplate
import java.io.File

/**
 * @author LoMu
 * Date 2025.02.04 20:39
 */
class TemplateRegister {
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
            val petpetTemplate = OldPetpetTemplate.fromJsonFile(it).toTemplate()

            for (petpetName in petpetTemplate.metadata.alias) {
                petpetTemplates[petpetName] = petpetTemplate
            }
        }
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
        }
        return jsonFiles
    }

}


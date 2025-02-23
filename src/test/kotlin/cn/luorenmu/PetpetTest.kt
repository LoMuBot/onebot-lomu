package cn.luorenmu

import cn.luorenmu.action.petpet.TemplateRegister
import cn.luorenmu.file.InitializeFile
import moe.dituon.petpet.template.PetpetTemplate

/**
 * @author LoMu
 * Date 2025.02.23 20:54
 */
fun main() {
    InitializeFile.run(MainApplication::class.java)
    println(TemplateRegister.petpetTemplates)
}
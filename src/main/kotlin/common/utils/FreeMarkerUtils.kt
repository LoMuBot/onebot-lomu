package cn.luorenmu.common.utils

import freemarker.template.Configuration
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.util.Locale


object FreeMarkerUtils {
    private val cfg = Configuration(Configuration.VERSION_2_3_32)

    init {
        cfg.setClassForTemplateLoading(FreeMarkerUtils::class.java, "/static/ftlh")
        cfg.setEncoding(Locale.CHINA, StandardCharsets.UTF_8.name())
    }

    fun parseData(templateName: String, dataModel: Any?): String {
        val template = cfg.getTemplate(templateName)
        val sw = StringWriter()
        template.process(dataModel, sw)
        return sw.toString()
    }
}

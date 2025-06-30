package cn.luorenmu.config

import org.springframework.beans.factory.annotation.Configurable
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @author LoMu
 * Date 2025.06.17 16:28
 */
@Configurable
class WebConfig : WebMvcConfigurer
{
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images")
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css")
        registry.addResourceHandler("/javascript/**").addResourceLocations("classpath:/static/javascript")
    }
}
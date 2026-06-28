package tech.provokedynamic.wdusbmidterm

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.concurrent.TimeUnit

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@ConfigurationPropertiesScan("tech.provokedynamic.wdusbmidterm.config")
@EnableCaching(mode = AdviceMode.PROXY)
@ComponentScan(
    basePackages = [
        "tech.provokedynamic.wdusbmidterm.config",
        "tech.provokedynamic.wdusbmidterm.service",
        "tech.provokedynamic.wdusbmidterm.security",
        "tech.provokedynamic.wdusbmidterm.repository",
        "tech.provokedynamic.wdusbmidterm.controller",
        "tech.provokedynamic.wdusbmidterm.exception",
    ]
)
@EntityScan("tech.provokedynamic.wdusbmidterm.entity")
class WdusbMidtermApplication : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/css/")
            .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
    }
}

fun main(args: Array<String>) {
    runApplication<WdusbMidtermApplication>(*args)
}

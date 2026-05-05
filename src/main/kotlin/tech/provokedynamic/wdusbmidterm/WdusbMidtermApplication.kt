package tech.provokedynamic.wdusbmidterm

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourcePoolMetadataProvidersConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.persistence.autoconfigure.PersistenceExceptionTranslationAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.servlet.autoconfigure.HttpEncodingAutoConfiguration
import org.springframework.boot.servlet.autoconfigure.MultipartAutoConfiguration
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration
import org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration
import org.springframework.boot.transaction.autoconfigure.TransactionManagerCustomizationAutoConfiguration
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.concurrent.TimeUnit

@SpringBootConfiguration(proxyBeanMethods = false)
@ImportAutoConfiguration(
    classes = [
        AopAutoConfiguration::class,
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        DataSourcePoolMetadataProvidersConfiguration::class,
        HibernateJpaAutoConfiguration::class,
        DataJpaRepositoriesAutoConfiguration::class,
        PersistenceExceptionTranslationAutoConfiguration::class,
        CacheAutoConfiguration::class,
        FlywayAutoConfiguration::class,
        WebMvcAutoConfiguration::class,
        DispatcherServletAutoConfiguration::class,
        TomcatServletWebServerAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class,
        HttpEncodingAutoConfiguration::class,
        MultipartAutoConfiguration::class,
        ErrorMvcAutoConfiguration::class,
        JacksonAutoConfiguration::class,
        ValidationAutoConfiguration::class,
        TransactionAutoConfiguration::class,
        TransactionManagerCustomizationAutoConfiguration::class,
        PropertyPlaceholderAutoConfiguration::class,
        ConfigurationPropertiesAutoConfiguration::class,
        LifecycleAutoConfiguration::class,
    ]
)
@AutoConfigurationPackage(basePackages = ["tech.provokedynamic.wdusbmidterm"])
@EnableCaching(mode = AdviceMode.PROXY)
@ComponentScan(
    basePackages = [
        "tech.provokedynamic.wdusbmidterm.service",
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
    val ctx = runApplication<WdusbMidtermApplication>(*args)
}
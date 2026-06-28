package tech.provokedynamic.wdusbmidterm

import com.github.benmanes.caffeine.cache.Caffeine
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import tech.provokedynamic.wdusbmidterm.config.AppProperties
import java.util.concurrent.TimeUnit

@SpringBootTest
class WdusbMidtermApplicationTests {

    @Bean
    fun cacheManager(appProperties: AppProperties): CacheManager {
        val manager = CaffeineCacheManager(
            "authors:exists", "authors:paged", "authors:all", "authors:single", "authors:count"
        )
        manager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(appProperties.maintenance.cacheTtlMinutes.toLong(), TimeUnit.MINUTES)
                .maximumSize(1000)
        )
        return manager
    }
}

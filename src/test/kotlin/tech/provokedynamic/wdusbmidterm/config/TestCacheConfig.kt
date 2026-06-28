package tech.provokedynamic.wdusbmidterm.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@TestConfiguration
@Profile("dev")
class TestCacheConfig {
    @Bean
    fun cacheManager(): CacheManager = ConcurrentMapCacheManager(
        "authors:all", "authors:paged", "authors:single", "authors:exists", "authors:count",
        "genres:all", "genres:single", "genres:exists", "genres:count",
        "books:all", "books:paged", "books:single", "books:exists", "books:count"
    )
}
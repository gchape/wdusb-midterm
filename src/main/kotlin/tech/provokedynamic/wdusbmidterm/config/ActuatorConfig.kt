package tech.provokedynamic.wdusbmidterm.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.boot.health.contributor.Health
import org.springframework.boot.health.contributor.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository

@Configuration
class ActuatorConfig(
    private val appProperties: AppProperties,
    private val environment: Environment,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    /**
     * Custom health indicator that verifies the catalog has data.
     * Reports DOWN if the database is reachable but has no books (unexpected empty state).
     */
    @Bean
    fun catalogHealthIndicator(): HealthIndicator = HealthIndicator {
        try {
            val bookCount = bookRepository.countByDeletedAtNull()
            val authorCount = authorRepository.countByDeletedAtNull()
            Health.up()
                .withDetail("books", bookCount)
                .withDetail("authors", authorCount)
                .build()
        } catch (ex: Exception) {
            Health.down()
                .withDetail("error", ex.message ?: "Unknown error")
                .build()
        }
    }

    /**
     * Exposes app metadata and active feature flags at /actuator/info.
     */
    @Bean
    fun appInfoContributor(): InfoContributor = InfoContributor { builder: Info.Builder ->
        builder.withDetail(
            "app", mapOf(
                "title" to appProperties.title,
                "description" to appProperties.description,
                "organization" to appProperties.organization,
                "contact" to appProperties.contactEmail,
                "profiles" to environment.activeProfiles.toList().ifEmpty { listOf("default") }
            )
        )
        builder.withDetail(
            "features", mapOf(
                "swagger" to appProperties.features.swaggerEnabled,
                "registration" to appProperties.features.registrationEnabled,
                "catalogPublic" to appProperties.features.catalogPublic
            )
        )
    }

    /**
     * Custom Micrometer metric: tracks total active book count as a gauge.
     */
    @Bean
    fun catalogMetrics(): MeterBinder = MeterBinder { registry: MeterRegistry ->
        registry.gauge(
            "library.books.active.total",
            bookRepository
        ) { repo -> repo.countByDeletedAtNull().toDouble() }
        registry.gauge(
            "library.authors.active.total",
            authorRepository
        ) { repo -> repo.countByDeletedAtNull().toDouble() }
    }
}

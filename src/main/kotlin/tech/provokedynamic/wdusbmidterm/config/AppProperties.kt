package tech.provokedynamic.wdusbmidterm.config

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "app")
data class AppProperties(

    @field:NotBlank(message = "app.title must not be blank")
    val title: String = "Library System",

    @field:NotBlank(message = "app.description must not be blank")
    val description: String = "A web-based library catalog",

    @field:Min(value = 1, message = "app.pagination.default-page-size must be at least 1")
    val defaultPageSize: Int = 12,

    @field:Min(value = 1, message = "app.pagination.max-page-size must be at least 1")
    val maxPageSize: Int = 100,

    @field:NotBlank(message = "app.contact.email must not be blank")
    @field:Email(message = "app.contact.email must be a valid email address")
    val contactEmail: String = "admin@example.com",

    @field:NotBlank(message = "app.contact.organization must not be blank")
    val organization: String = "Library System",

    val features: Features = Features(),

    val maintenance: Maintenance = Maintenance()
) {
    data class Features(
        val swaggerEnabled: Boolean = true,
        val registrationEnabled: Boolean = true,
        val catalogPublic: Boolean = true
    )

    data class Maintenance(
        @field:Positive(message = "app.maintenance.cache-ttl-minutes must be positive")
        val cacheTtlMinutes: Int = 10,
        val showSqlInLogs: Boolean = false
    )
}

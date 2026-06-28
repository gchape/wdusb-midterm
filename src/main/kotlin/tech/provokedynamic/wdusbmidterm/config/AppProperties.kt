package tech.provokedynamic.wdusbmidterm.config

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component
@Validated
@ConfigurationProperties(prefix = "app")
class AppProperties {

    @field:NotBlank(message = "app.title must not be blank")
    var title: String = "Library System"

    @field:NotBlank(message = "app.description must not be blank")
    var description: String = "A web-based library catalog"

    @field:Min(value = 1, message = "app.pagination.default-page-size must be at least 1")
    var defaultPageSize: Int = 12

    @field:Min(value = 1, message = "app.pagination.max-page-size must be at least 1")
    var maxPageSize: Int = 100

    @field:NotBlank(message = "app.contact.email must not be blank")
    @field:Email(message = "app.contact.email must be a valid email address")
    var contactEmail: String = "admin@example.com"

    @field:NotBlank(message = "app.contact.organization must not be blank")
    var organization: String = "Library System"

    var features: Features = Features()

    var maintenance: Maintenance = Maintenance()

    class Features {
        var swaggerEnabled: Boolean = true
        var registrationEnabled: Boolean = true
        var catalogPublic: Boolean = true
    }

    class Maintenance {
        @field:Positive(message = "app.maintenance.cache-ttl-minutes must be positive")
        var cacheTtlMinutes: Int = 10
        var showSqlInLogs: Boolean = false
    }
}

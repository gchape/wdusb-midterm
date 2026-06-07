package tech.provokedynamic.wdusbmidterm.controller.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.provokedynamic.wdusbmidterm.config.AppProperties
import tech.provokedynamic.wdusbmidterm.dto.response.AppInfoResponse

@RestController
@RequestMapping("/api/info")
@Tag(name = "Info", description = "Application metadata and feature flags")
class InfoRestController(
    private val appProperties: AppProperties,
    private val environment: Environment
) {

    @GetMapping
    @Operation(
        summary = "Get application info",
        description = "Returns metadata, active profile, and feature flags from externalized config"
    )
    fun getInfo(): ResponseEntity<AppInfoResponse> {
        val activeProfiles = environment.activeProfiles.toList()
            .ifEmpty { listOf("default") }

        return ResponseEntity.ok(
            AppInfoResponse(
                title = appProperties.title,
                description = appProperties.description,
                organization = appProperties.organization,
                contactEmail = appProperties.contactEmail,
                activeProfiles = activeProfiles,
                features = AppInfoResponse.FeaturesDto(
                    swaggerEnabled = appProperties.features.swaggerEnabled,
                    registrationEnabled = appProperties.features.registrationEnabled,
                    catalogPublic = appProperties.features.catalogPublic
                ),
                pagination = AppInfoResponse.PaginationDto(
                    defaultPageSize = appProperties.defaultPageSize,
                    maxPageSize = appProperties.maxPageSize
                )
            )
        )
    }
}

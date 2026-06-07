package tech.provokedynamic.wdusbmidterm.dto.response

data class AppInfoResponse(
    val title: String,
    val description: String,
    val organization: String,
    val contactEmail: String,
    val activeProfiles: List<String>,
    val features: FeaturesDto,
    val pagination: PaginationDto
) {
    data class FeaturesDto(
        val swaggerEnabled: Boolean,
        val registrationEnabled: Boolean,
        val catalogPublic: Boolean
    )

    data class PaginationDto(
        val defaultPageSize: Int,
        val maxPageSize: Int
    )
}

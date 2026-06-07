package tech.provokedynamic.wdusbmidterm.dto.request

import jakarta.validation.constraints.NotBlank

data class PublisherRequest(
    @field:NotBlank(message = "{publisher.name.notBlank}")
    val name: String = ""
)

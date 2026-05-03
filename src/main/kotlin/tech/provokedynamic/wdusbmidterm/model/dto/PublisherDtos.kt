package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.NotBlank

data class PublisherRequest(
    @field:NotBlank(message = "Name is required")
    val name: String = ""
)
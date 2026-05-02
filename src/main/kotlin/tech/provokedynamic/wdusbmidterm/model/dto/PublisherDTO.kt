package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.NotBlank
import tech.provokedynamic.wdusbmidterm.entity.Publisher

data class PublisherResponseDTO(
    val id: Long,
    val name: String
)

data class PublisherRequestDTO(
    @field:NotBlank(message = "Name is required")
    val name: String = ""
)

fun Publisher.toResponseDto() = PublisherResponseDTO(id = id, name = name)
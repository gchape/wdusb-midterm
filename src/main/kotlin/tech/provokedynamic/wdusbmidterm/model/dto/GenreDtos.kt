package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GenreRequest(
    @field:NotBlank(message = "Genre name is required")
    @field:Size(max = 50, message = "Genre name cannot exceed 50 characters")
    val name: String
)

package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import tech.provokedynamic.wdusbmidterm.entity.Genre

data class GenreResponseDTO(
    var id: Long,
    val name: String
)

data class GenreRequestDTO(
    @field:NotBlank(message = "Genre name is required")
    @field:Size(max = 50, message = "Genre name cannot exceed 50 characters")
    val name: String
)

fun Genre.toResponseDto(): GenreResponseDTO =
    GenreResponseDTO(
        id = this.id,
        name = this.name
    )
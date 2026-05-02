package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import tech.provokedynamic.wdusbmidterm.entity.Author

data class AuthorResponseDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val bio: String?,
)

data class AuthorRequestDTO(
    @field:NotBlank(message = "First name can not be blank")
    @field:Size(min = 2, message = "First name must be at least 2 characters")
    @field:Size(max = 255, message = "First name cannot exceed 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name can not be blank")
    @field:Size(min = 2, message = "Last name must be at least 2 characters")
    @field:Size(max = 60, message = "Last name cannot exceed 1000 characters")
    val lastName: String,

    @field:Size(max = 5000, message = "Bio is too long")
    val bio: String?,
)

fun Author.toResponseDto(): AuthorResponseDTO =
    AuthorResponseDTO(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        bio = this.bio,
    )
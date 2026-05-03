package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthorRequest(
    @field:NotBlank(message = "First name can not be blank")
    @field:Size(min = 2, message = "First name must be at least 2 characters")
    @field:Size(max = 255, message = "First name cannot exceed 255 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name can not be blank")
    @field:Size(min = 2, message = "Last name must be at least 2 characters")
    @field:Size(max = 60, message = "Last name cannot exceed 60 characters")
    val lastName: String,

    @field:Size(max = 5000, message = "Bio is too long")
    val bio: String?,
)

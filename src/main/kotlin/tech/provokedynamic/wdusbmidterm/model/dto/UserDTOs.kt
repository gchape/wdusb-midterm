package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserResponseDTO(
    val id: Long,
    val userName: String,
    val email: String,
)

data class UserRequestDTO(
    @field:NotBlank(message = "Username is required")
    @field:Size(max = 50, message = "Username cannot exceed 50 characters")
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Must be a valid email address")
    @field:Size(max = 255, message = "Email cannot exceed 255 characters")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    val password: String
)
package tech.provokedynamic.wdusbmidterm.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "{register.username.notBlank}")
    @field:Size(min = 3, max = 50, message = "{register.username.size}")
    val username: String,

    @field:NotBlank(message = "{register.password.notBlank}")
    @field:Size(min = 6, message = "{register.password.size.min}")
    val password: String,

    @field:NotBlank(message = "{register.confirmPassword.notBlank}")
    val confirmPassword: String
) {
    val passwordsMatch: Boolean get() = password == confirmPassword
}

package tech.provokedynamic.wdusbmidterm.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthorRequest(
    @field:NotBlank(message = "{author.firstName.notBlank}")
    @field:Size(min = 2, message = "{author.firstName.size.min}")
    @field:Size(max = 255, message = "{author.firstName.size.max}")
    val firstName: String,

    @field:NotBlank(message = "{author.lastName.notBlank}")
    @field:Size(min = 2, message = "{author.lastName.size.min}")
    @field:Size(max = 60, message = "{author.lastName.size.max}")
    val lastName: String,

    @field:Size(max = 5000, message = "{author.bio.size.max}")
    val bio: String?,
)

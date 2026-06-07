package tech.provokedynamic.wdusbmidterm.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GenreRequest(
    @field:NotBlank(message = "{genre.name.notBlank}")
    @field:Size(max = 50, message = "{genre.name.size.max}")
    val name: String
)

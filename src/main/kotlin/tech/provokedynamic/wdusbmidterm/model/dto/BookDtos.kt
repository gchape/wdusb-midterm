package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.*
import java.time.LocalDate

data class BookCreateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "ISBN is required")
    @field:Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
    val isbn: String,

    @field:NotNull(message = "Publisher ID is required")
    @field:Positive(message = "Invalid Publisher ID")
    var publisherId: Long,

    @field:NotNull(message = "Publication date is required")
    @field:PastOrPresent(message = "Publication date cannot be in the future")
    var publicationDate: LocalDate,

    @field:NotNull(message = "Page count is required")
    @field:Min(value = 1, message = "Page count must be at least 1")
    var pageCount: Short,

    @field:NotEmpty(message = "A book must have at least one genre")
    val genreIds: List<Long>,

    @field:NotEmpty(message = "A book must have at least one author")
    val authorIds: List<Long>
)
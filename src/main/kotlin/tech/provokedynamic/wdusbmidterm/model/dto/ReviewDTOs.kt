package tech.provokedynamic.wdusbmidterm.model.dto

import jakarta.validation.constraints.*

data class ReviewResponseDTOs(
    val id: Long,
    val userId: Long,
    val rating: Float,
    val body: String?,
)

data class ReviewRequestDTO(
    @field:NotNull(message = "User ID is required")
    @field:Positive(message = "Invalid User ID")
    var userId: Long,

    @field:NotNull(message = "Book ID is required")
    @field:Positive(message = "Invalid Book ID")
    var bookId: Long,

    @field:NotNull(message = "Rating is required")
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating cannot exceed 5")
    var rating: Short,

    @field:Size(max = 2000, message = "Review body is too long")
    val body: String?
)
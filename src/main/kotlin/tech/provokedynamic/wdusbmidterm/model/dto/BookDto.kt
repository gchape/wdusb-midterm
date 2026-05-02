package tech.provokedynamic.wdusbmidterm.model

import tech.provokedynamic.wdusbmidterm.entity.Book
import java.time.LocalDate

data class BookRequestDto(
    val title: String,
    val isbn: String,
    val publishedAt: LocalDate,
    val pageCount: Int,
    val genre: String? = "Uncategorized",
)

data class BookResponseDto(
    val id: Int,
    val title: String,
    val isbn: String,
    val genre: String?,
    val pageCount: Int?,
    val publishedAt: LocalDate?,
    val authors: List<AuthorResponseDto>
)

fun Book.toResponseDto(): BookResponseDto {
    return BookResponseDto(
        id = this.id,
        title = this.title,
        isbn = this.isbn,
        publishedAt = this.publishedAt,
        pageCount = this.pageCount,
        genre = this.genre,
        authors = this.authors.map { it.toAuthorResponseDto() }
    )
}
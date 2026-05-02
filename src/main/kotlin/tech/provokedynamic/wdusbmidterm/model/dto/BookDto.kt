package tech.provokedynamic.wdusbmidterm.model.dto

import tech.provokedynamic.wdusbmidterm.entity.Book
import java.time.LocalDate

data class BookRequestDto(
    val title: String,
    val isbn: String,
    val publishedAt: LocalDate,
    val pageCount: Int,
    val genre: String? = null,
    val authorIds: List<Int> = emptyList(),
)

data class BookResponseDto(
    val id: Int,
    val title: String,
    val isbn: String,
    val genre: String?,
    val pageCount: Int?,
    val publishedAt: LocalDate,
    val authors: List<AuthorResponseDto> = emptyList(),
)

fun Book.toResponseDto() = BookResponseDto(
    id = id,
    title = title,
    isbn = isbn,
    publishedAt = publishedAt,
    pageCount = pageCount,
    genre = genre,
    authors = authors.map { it.toAuthorResponseDto() }
)
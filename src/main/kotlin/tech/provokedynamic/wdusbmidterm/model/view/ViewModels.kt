package tech.provokedynamic.wdusbmidterm.model.view

import org.springframework.validation.BindingResult
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorResponseDTO
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDTO

data class BookCatalogViewModel(
    val books: List<BookResponseDTO>,
    val totalCount: Long,
    val currentPage: Int,
    val totalPages: Int,
)

data class AuthorIndexViewModel(
    val authors: List<AuthorResponseDTO>,
    val totalCount: Long,
    val currentPage: Int,
    val totalPages: Int
)

fun BindingResult.toErrorMap(): Map<String, String> =
    fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
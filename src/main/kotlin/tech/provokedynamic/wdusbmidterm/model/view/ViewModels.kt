package tech.provokedynamic.wdusbmidterm.model.view

import org.springframework.validation.BindingResult
import tech.provokedynamic.wdusbmidterm.model.projection.AuthorResponse
import tech.provokedynamic.wdusbmidterm.model.projection.BookCatalogItem

data class BookCatalogViewModel(
    val books: Set<BookCatalogItem>,
    val totalCount: Long,
    val currentPage: Int,
    val totalPages: Int,
)

data class AuthorIndexViewModel(
    val authors: Set<AuthorResponse>,
    val totalCount: Long,
    val currentPage: Int,
    val totalPages: Int,
)

fun BindingResult.toErrorMap(): Map<String, String> =
    fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
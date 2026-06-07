package tech.provokedynamic.wdusbmidterm.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tech.provokedynamic.wdusbmidterm.config.AppProperties
import tech.provokedynamic.wdusbmidterm.dto.response.BookCardResponse

@Service
class HomeService(
    private val bookService: BookService,
    private val authorService: AuthorService,
    private val appProperties: AppProperties
) {
    fun getRecentBooks(): List<BookCardResponse> =
        bookService.getRecentBooks()

    fun getTotalBooks(): Long = bookService.getTotalBooks()

    fun getTotalAuthors(): Long = authorService.getTotalAuthors()

    fun defaultCatalogPageRequest(): PageRequest =
        PageRequest.of(
            0,
            appProperties.defaultPageSize,
            Sort.by(Sort.Direction.DESC, "publicationDate")
        )
}

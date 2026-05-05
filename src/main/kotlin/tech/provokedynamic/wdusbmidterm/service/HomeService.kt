package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import tech.provokedynamic.wdusbmidterm.dto.response.BookCardResponse

@Service
class HomeService(
    private val bookService: BookService,
    private val authorService: AuthorService,
) {
    fun getRecentBooks(): List<BookCardResponse> =
        bookService.getRecentBooks()

    fun getTotalBooks(): Long = bookService.getTotalBooks()

    fun getTotalAuthors(): Long = authorService.getTotalAuthors()
}
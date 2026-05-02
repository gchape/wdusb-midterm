package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import tech.provokedynamic.wdusbmidterm.model.dto.BookRequestDto
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDto
import tech.provokedynamic.wdusbmidterm.service.BookService

@RestController
@RequestMapping(path = ["/api/books"], produces = [MediaType.APPLICATION_JSON_VALUE])
class BookController(val bookService: BookService) {

    @GetMapping("/{isbn}")
    fun getBookByIsbn(
        @PathVariable isbn: String
    ): BookResponseDto? = bookService.getBookByIsbn(isbn)

    @GetMapping
    fun getAllBooks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) genre: String?,
        @RequestParam(required = false) query: String?,
    ): Page<BookResponseDto> {
        val pageable = PageRequest.of(page, pageSize)
        return when {
            query != null -> bookService.searchBooks(query, pageable)
            genre != null -> bookService.getBooksByGenre(genre, pageable)
            else -> bookService.getAllBooks(pageable)
        }
    }

    @GetMapping("/recent")
    fun getRecentBooks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int
    ): List<BookResponseDto> {
        val pageable = PageRequest.of(page, pageSize)
        return bookService.getRecentBooks(pageable)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody request: BookRequestDto): BookResponseDto =
        bookService.createBook(request)
}
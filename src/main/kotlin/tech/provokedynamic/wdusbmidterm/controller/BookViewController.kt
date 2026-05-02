package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import tech.provokedynamic.wdusbmidterm.service.BookService

@Controller
@RequestMapping("/books")
class BookViewController(
    val bookService: BookService,
) {

    @GetMapping
    fun books(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) genre: String?,
        @RequestParam(required = false) query: String?,
    ): String {
        val pageable = PageRequest.of(page, pageSize)
        val books = when {
            query != null -> bookService.searchBooks(query, pageable)
            genre != null -> bookService.getBooksByGenre(genre, pageable)
            else -> bookService.getAllBooks(pageable)
        }
        model.addAttribute("books", books)
        model.addAttribute("genre", genre)
        model.addAttribute("query", query)
        return "books"
    }

    @GetMapping("/{isbn}")
    fun bookDetail(
        model: Model,
        @PathVariable isbn: String
    ): String {
        val book = bookService.getBookByIsbn(isbn)
            ?: return "redirect:/books"
        model.addAttribute("book", book)
        return "book"
    }
}
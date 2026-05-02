package tech.provokedynamic.wdusbmidterm.controller.view

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDto
import tech.provokedynamic.wdusbmidterm.model.response.PageResponse

@Controller
@RequestMapping("/books")
class BookViewController(
    @Value("\${app.api.base-url}") private val baseUrl: String
) {
    private val restClient: RestClient by lazy { RestClient.create(baseUrl) }

    @GetMapping
    fun showBooksPage(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) genre: String?,
        @RequestParam(required = false) query: String?,
    ): String {
        val uri = buildString {
            append("/api/books?page=$page&pageSize=$pageSize")
            query?.let { append("&query=$it") }
            genre?.let { append("&genre=$it") }
        }

        val books = restClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body<PageResponse<BookResponseDto>>()

        model.addAttribute("books", books)
        model.addAttribute("genre", genre)
        model.addAttribute("query", query)
        return "books"
    }

    @GetMapping("/{isbn}")
    fun showBookDetailPage(
        model: Model,
        @PathVariable isbn: String,
    ): String {
        val book = restClient.get()
            .uri("/api/books/$isbn")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body<BookResponseDto>()
            ?: return "redirect:/books"

        model.addAttribute("book", book)
        return "book"
    }
}
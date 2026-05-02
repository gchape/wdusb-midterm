package tech.provokedynamic.wdusbmidterm.controller.view

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorResponseDto
import tech.provokedynamic.wdusbmidterm.model.dto.BookRequestDto

@Controller
@RequestMapping("/books/form")
class BookFormViewController(
    @Value("\${app.api.base-url}") private val baseUrl: String
) {
    private val restClient: RestClient by lazy { RestClient.create(baseUrl) }

    private fun fetchAllAuthors(): List<AuthorResponseDto> =
        restClient.get()
            .uri("/api/authors")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body<List<AuthorResponseDto>>()
            ?: emptyList()

    @GetMapping
    fun showCreateBookForm(model: Model): String {
        model.addAttribute("authors", fetchAllAuthors())
        model.addAttribute("error", null)
        return "books-form"
    }

    @PostMapping
    fun submitCreateBookForm(
        @ModelAttribute request: BookRequestDto,
        model: Model,
    ): String {
        return try {
            restClient.post()
                .uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity()
            "redirect:/books"
        } catch (e: Exception) {
            model.addAttribute("authors", fetchAllAuthors())
            model.addAttribute("error", e.message)
            "books-form"
        }
    }
}
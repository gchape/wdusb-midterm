package tech.provokedynamic.wdusbmidterm.controller.view

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDto

@Controller
@RequestMapping("/")
class HomeViewController(
    @Value("\${app.api.base-url}") private val baseUrl: String
) {
    private val restClient: RestClient by lazy { RestClient.create(baseUrl) }

    @GetMapping(produces = ["text/html;charset=utf-8"])
    fun showHomePage(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
    ): String {
        val recentBooks = restClient.get()
            .uri("/api/books/recent?page=$page&pageSize=$pageSize")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body<List<BookResponseDto>>()
            ?: emptyList()

        model.addAttribute("recentBooks", recentBooks)
        return "home"
    }
}
package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDto

@Controller
@RequestMapping("/")
class HomeController {

    val restClient: RestClient = RestClient.create("http://localhost:8080/api")

    @GetMapping(produces = ["text/html;charset=utf-8"])
    fun home(model: Model): String {
        val recentBooks = restClient.get()
            .uri("/books/recent")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body<List<BookResponseDto>>()

        model.addAttribute("recentBooks", recentBooks)

        return "home"
    }
}
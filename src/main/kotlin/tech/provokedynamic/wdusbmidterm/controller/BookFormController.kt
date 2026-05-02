package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import tech.provokedynamic.wdusbmidterm.model.dto.BookRequestDto
import tech.provokedynamic.wdusbmidterm.service.AuthorService
import tech.provokedynamic.wdusbmidterm.service.BookService

@Controller
@RequestMapping("/books/form")
class BookFormController(val authorService: AuthorService, val bookService: BookService) {

    @GetMapping
    fun newBookForm(model: Model): String {
        model.addAttribute("authors", authorService.getAllAuthors())
        model.addAttribute("error", null)
        return "books-form"
    }

    @PostMapping
    fun createBook(
        @ModelAttribute request: BookRequestDto,
        model: Model,
    ): String {
        return try {
            bookService.createBook(request)
            "redirect:/books"
        } catch (e: Exception) {
            model.addAttribute("authors", authorService.getAllAuthors())
            model.addAttribute("error", e.message)
            "books-form"
        }
    }

}
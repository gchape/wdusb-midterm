package tech.provokedynamic.wdusbmidterm.controller

import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import tech.provokedynamic.wdusbmidterm.model.dto.BookCreateRequest
import tech.provokedynamic.wdusbmidterm.model.view.BookCatalogViewModel
import tech.provokedynamic.wdusbmidterm.model.view.toErrorMap
import tech.provokedynamic.wdusbmidterm.service.AuthorService
import tech.provokedynamic.wdusbmidterm.service.BookService
import tech.provokedynamic.wdusbmidterm.service.GenreService
import tech.provokedynamic.wdusbmidterm.service.PublisherService

@Controller
@RequestMapping("/books")
class BookController(
    private val bookService: BookService,
    private val genreService: GenreService,
    private val authorService: AuthorService,
    private val publisherService: PublisherService
) {
    @GetMapping
    fun catalog(
        @RequestParam(defaultValue = "1") page: Int,
        model: Model
    ): String {
        val pageRequest = PageRequest.of(page - 1, 12, Sort.by(Sort.Direction.DESC, "publicationDate"))
        val booksPage = bookService.getCatalog(pageRequest)
        model.addAttribute(
            "vm", BookCatalogViewModel(
                currentPage = page,
                books = booksPage.content.toSet(),
                totalCount = booksPage.totalElements,
                totalPages = booksPage.totalPages,
            )
        )
        return "books/index"
    }

    @GetMapping("/{id}")
    fun showBook(@PathVariable id: Long, model: Model): String {
        model.addAttribute("book", bookService.getBookById(id))
        return "books/show"
    }

    @GetMapping("/new")
    fun newBookForm(model: Model): String {
        model.addAttribute("book", null)
        model.addAttribute("publishers", publisherService.getAllPublishers())
        model.addAttribute("authors", authorService.getAllAuthors())
        model.addAttribute("genres", genreService.getAllGenres())
        return "books/form"
    }

    @PostMapping("/new")
    fun createBook(
        @Valid @ModelAttribute request: BookCreateRequest,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", null)
            model.addAttribute("publishers", publisherService.getAllPublishers())
            model.addAttribute("authors", authorService.getAllAuthors())
            model.addAttribute("genres", genreService.getAllGenres())
            model.addAttribute("errors", bindingResult.toErrorMap())
            return "books/form"
        }
        val saved = bookService.createBook(request)
        redirectAttributes.addFlashAttribute("flashSuccess", "\"${saved.title}\" was added to the catalog.")
        return "redirect:/books/${saved.id}"
    }

    @GetMapping("/{id}/edit")
    fun editBookForm(@PathVariable id: Long, model: Model): String {
        model.addAttribute("book", bookService.getBookById(id))
        model.addAttribute("publishers", publisherService.getAllPublishers())
        model.addAttribute("authors", authorService.getAllAuthors())
        model.addAttribute("genres", genreService.getAllGenres())
        return "books/form"
    }

    @PostMapping("/{id}/edit")
    fun updateBook(
        @PathVariable id: Long,
        @Valid @ModelAttribute request: BookCreateRequest,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", bookService.getBookById(id))
            model.addAttribute("publishers", publisherService.getAllPublishers())
            model.addAttribute("authors", authorService.getAllAuthors())
            model.addAttribute("genres", genreService.getAllGenres())
            model.addAttribute("errors", bindingResult.toErrorMap())
            return "books/form"
        }
        val updated = bookService.updateBook(id, request)
        redirectAttributes.addFlashAttribute("flashSuccess", "\"${updated.title}\" was updated.")
        return "redirect:/books/${updated.id}"
    }

    @PostMapping("/{id}/delete")
    fun deleteBook(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        bookService.softDeleteBook(id)
        redirectAttributes.addFlashAttribute("flashSuccess", "Book was deleted.")
        return "redirect:/books"
    }
}
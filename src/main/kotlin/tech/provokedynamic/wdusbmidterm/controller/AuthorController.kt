package tech.provokedynamic.wdusbmidterm.controller

import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorRequestDTO
import tech.provokedynamic.wdusbmidterm.model.view.AuthorIndexViewModel
import tech.provokedynamic.wdusbmidterm.model.view.toErrorMap
import tech.provokedynamic.wdusbmidterm.service.AuthorService

@Controller
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService
) {
    @GetMapping
    fun index(
        @RequestParam(defaultValue = "1") page: Int,
        model: Model
    ): String {
        val pageRequest = PageRequest.of(page - 1, 18, Sort.by(Sort.Direction.ASC, "lastName"))
        val authorsPage = authorService.getAuthors(pageRequest)

        model.addAttribute(
            "vm", AuthorIndexViewModel(
                authors = authorsPage.content,
                totalCount = authorsPage.totalElements,
                currentPage = page,
                totalPages = authorsPage.totalPages
            )
        )
        return "authors/index"
    }

    @GetMapping("/{id}")
    fun showAuthor(@PathVariable id: Long, model: Model): String {
        model.addAttribute("author", authorService.getAuthorById(id))
        model.addAttribute("books", authorService.getBooksForAuthor(id))
        return "authors/show"
    }

    @GetMapping("/add")
    fun newAuthorForm(model: Model): String {
        model.addAttribute("author", null)
        return "authors/form"
    }

    @PostMapping("/add")
    fun createAuthor(
        @Valid @ModelAttribute request: AuthorRequestDTO,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("author", null)
            model.addAttribute("errors", bindingResult.toErrorMap())
            return "authors/form"
        }
        return try {
            val saved = authorService.createAuthor(request)
            redirectAttributes.addFlashAttribute(
                "flashSuccess",
                "${saved.firstName} ${saved.lastName} was added."
            )
            "redirect:/authors/${saved.id}"
        } catch (ex: Exception) {
            model.addAttribute("author", null)
            model.addAttribute("flashError", "Could not save author: ${ex.message}")
            "authors/form"
        }
    }

    @GetMapping("/{id}/edit")
    fun editAuthorForm(@PathVariable id: Long, model: Model): String {
        model.addAttribute("author", authorService.getAuthorById(id))
        return "authors/form"
    }

    @PostMapping("/{id}/edit")
    fun updateAuthor(
        @PathVariable id: Long,
        @Valid @ModelAttribute request: AuthorRequestDTO,
        bindingResult: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("author", authorService.getAuthorById(id))
            model.addAttribute("errors", bindingResult.toErrorMap())
            return "authors/form"
        }
        return try {
            val updated = authorService.updateAuthor(id, request)
            redirectAttributes.addFlashAttribute(
                "flashSuccess",
                "${updated.firstName} ${updated.lastName} was updated."
            )
            "redirect:/authors/${updated.id}"
        } catch (ex: Exception) {
            model.addAttribute("author", authorService.getAuthorById(id))
            model.addAttribute("flashError", "Could not update author: ${ex.message}")
            "authors/form"
        }
    }

    @PostMapping("/{id}/delete")
    fun deleteAuthor(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        authorService.softDeleteAuthor(id)
        redirectAttributes.addFlashAttribute("flashSuccess", "Author was deleted.")
        return "redirect:/authors"
    }
}
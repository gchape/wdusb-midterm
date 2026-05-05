package tech.provokedynamic.wdusbmidterm.controller.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.provokedynamic.wdusbmidterm.dto.request.AuthorRequest
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorBookResponse
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorResponse
import tech.provokedynamic.wdusbmidterm.service.AuthorService

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors", description = "CRUD operations for authors")
class AuthorRestController(
    private val authorService: AuthorService
) {
    @GetMapping
    @Operation(summary = "Get all authors", description = "Returns a paginated list of all non-deleted authors")
    fun getAll(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "18") size: Int
    ): Page<AuthorResponse> {
        val pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "lastName"))
        return authorService.getAuthors(pageRequest)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Returns a single author by ID")
    fun getById(@PathVariable id: Long): AuthorResponse =
        authorService.getAuthorById(id)

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by author", description = "Returns all non-deleted books for a given author")
    fun getBooks(@PathVariable id: Long): List<AuthorBookResponse> =
        authorService.getBooksForAuthor(id)

    @PostMapping
    @Operation(summary = "Create an author", description = "Creates a new author and returns it")
    fun create(@Valid @RequestBody request: AuthorRequest): ResponseEntity<AuthorResponse> {
        val saved = authorService.createAuthor(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an author", description = "Updates an existing author by ID")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: AuthorRequest
    ): AuthorResponse = authorService.updateAuthor(id, request)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Soft-deletes an author by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = authorService.softDeleteAuthor(id)
}
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
import tech.provokedynamic.wdusbmidterm.dto.request.BookRequest
import tech.provokedynamic.wdusbmidterm.dto.response.BookCatalogResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookDetailResponse
import tech.provokedynamic.wdusbmidterm.service.BookService

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "CRUD operations for books")
class BookRestController(
    private val bookService: BookService
) {
    @GetMapping
    @Operation(summary = "Get all books", description = "Returns a paginated list of all non-deleted books")
    fun getAll(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "12") size: Int
    ): Page<BookCatalogResponse> {
        val pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "publicationDate"))
        return bookService.getBooks(pageRequest)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Returns full detail for a single book")
    fun getById(@PathVariable id: Long): BookDetailResponse =
        bookService.getBookById(id)

    @PostMapping
    @Operation(summary = "Create a book", description = "Creates a new book and returns its detail")
    fun create(@Valid @RequestBody request: BookRequest): ResponseEntity<BookDetailResponse> {
        val saved = bookService.createBook(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: BookRequest
    ): BookDetailResponse = bookService.updateBook(id, request)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Soft-deletes a book by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = bookService.deleteBook(id)
}
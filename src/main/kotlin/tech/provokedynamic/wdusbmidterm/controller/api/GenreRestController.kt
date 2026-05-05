package tech.provokedynamic.wdusbmidterm.controller.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.provokedynamic.wdusbmidterm.dto.request.GenreRequest
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse
import tech.provokedynamic.wdusbmidterm.service.GenreService

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "CRUD operations for genres")
class GenreRestController(
    private val genreService: GenreService
) {
    @GetMapping
    @Operation(summary = "Get all genres", description = "Returns all genres sorted by name")
    fun getAll(): List<GenreResponse> = genreService.getAllGenres()

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID", description = "Returns a single genre by ID")
    fun getById(@PathVariable id: Long): GenreResponse = genreService.getGenreById(id)

    @PostMapping
    @Operation(summary = "Create a genre", description = "Creates a new genre and returns it")
    fun create(@Valid @RequestBody request: GenreRequest): ResponseEntity<GenreResponse> {
        val saved = genreService.createGenre(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a genre", description = "Updates an existing genre by ID")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: GenreRequest
    ): GenreResponse = genreService.updateGenre(id, request)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre", description = "Deletes a genre by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = genreService.deleteGenre(id)
}
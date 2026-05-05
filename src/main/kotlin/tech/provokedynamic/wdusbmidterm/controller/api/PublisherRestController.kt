package tech.provokedynamic.wdusbmidterm.controller.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.provokedynamic.wdusbmidterm.dto.request.PublisherRequest
import tech.provokedynamic.wdusbmidterm.dto.response.PublisherResponse
import tech.provokedynamic.wdusbmidterm.service.PublisherService

@RestController
@RequestMapping("/api/publishers")
@Tag(name = "Publishers", description = "CRUD operations for publishers")
class PublisherRestController(
    private val publisherService: PublisherService
) {
    @GetMapping
    @Operation(summary = "Get all publishers", description = "Returns all non-deleted publishers sorted by name")
    fun getAll(): List<PublisherResponse> = publisherService.getAllPublishers()

    @GetMapping("/{id}")
    @Operation(summary = "Get publisher by ID", description = "Returns a single publisher by ID")
    fun getById(@PathVariable id: Long): PublisherResponse = publisherService.getPublisherById(id)

    @PostMapping
    @Operation(summary = "Create a publisher", description = "Creates a new publisher and returns it")
    fun create(@Valid @RequestBody request: PublisherRequest): ResponseEntity<PublisherResponse> {
        val saved = publisherService.createPublisher(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a publisher", description = "Updates an existing publisher by ID")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: PublisherRequest
    ): PublisherResponse = publisherService.updatePublisher(id, request)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a publisher", description = "Soft-deletes a publisher by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = publisherService.deletePublisher(id)
}
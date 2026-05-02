package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorResponseDto
import tech.provokedynamic.wdusbmidterm.service.AuthorService

@RestController
@RequestMapping(path = ["/api/authors"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthorController(val authorService: AuthorService) {

    @GetMapping
    fun getAllAuthors(): List<AuthorResponseDto> = authorService.getAllAuthors()
}
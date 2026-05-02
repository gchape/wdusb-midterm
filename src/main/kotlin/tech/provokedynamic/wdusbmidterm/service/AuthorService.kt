package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorResponseDto
import tech.provokedynamic.wdusbmidterm.model.dto.toAuthorResponseDto
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository

@Service
class AuthorService(val authorRepository: AuthorRepository) {

    fun getAllAuthors(): List<AuthorResponseDto> =
        authorRepository.findAllByDeletedFalse()
            .map { it.toAuthorResponseDto() }
}
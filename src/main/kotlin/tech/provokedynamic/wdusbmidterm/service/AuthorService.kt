package tech.provokedynamic.wdusbmidterm.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.entity.Author
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorRequestDTO
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorResponseDTO
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDTO
import tech.provokedynamic.wdusbmidterm.model.dto.toResponseDto
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import java.time.Instant

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {
    @Transactional(readOnly = true)
    fun getAllAuthors(): List<AuthorResponseDTO> =
        authorRepository.findAllByDeletedAtNull().map { it.toResponseDto() }

    @Transactional(readOnly = true)
    fun getAuthors(pageable: Pageable): Page<AuthorResponseDTO> =
        authorRepository.findAllByDeletedAtNull(pageable).map { it.toResponseDto() }

    @Transactional(readOnly = true)
    fun getAuthorById(id: Long): AuthorResponseDTO {
        val author = authorRepository.findById(id)
            .orElseThrow { NoSuchElementException("Author $id not found") }
        if (author.deletedAt != null) throw IllegalArgumentException("Author is deleted")
        return author.toResponseDto()
    }

    @Transactional(readOnly = true)
    fun getBooksForAuthor(authorId: Long): List<BookResponseDTO> =
        bookRepository.findAllByAuthorId(authorId).map { it.toResponseDto() }

    @Transactional(readOnly = true)
    fun countAuthors(): Long = authorRepository.countByDeletedAtNull()

    @Transactional
    fun createAuthor(request: AuthorRequestDTO): AuthorResponseDTO {
        val author = Author(
            firstName = request.firstName.trim(),
            lastName = request.lastName.trim(),
        )
        author.bio = request.bio?.trim()?.ifBlank { null }
        return authorRepository.save(author).toResponseDto()
    }

    @Transactional
    fun updateAuthor(id: Long, request: AuthorRequestDTO): AuthorResponseDTO {
        val author = authorRepository.findById(id)
            .orElseThrow { NoSuchElementException("Author $id not found") }
        if (author.deletedAt != null) throw IllegalArgumentException("Author is deleted")

        author.firstName = request.firstName.trim()
        author.lastName = request.lastName.trim()
        author.bio = request.bio?.trim()?.ifBlank { null }

        return authorRepository.save(author).toResponseDto()
    }

    @Transactional
    fun softDeleteAuthor(id: Long) {
        val author = authorRepository.findById(id)
            .orElseThrow { NoSuchElementException("Author $id not found") }
        author.deletedAt = Instant.now()
        authorRepository.save(author)
    }
}
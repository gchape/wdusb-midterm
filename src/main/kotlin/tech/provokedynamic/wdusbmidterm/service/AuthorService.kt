package tech.provokedynamic.wdusbmidterm.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.entity.Author
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.model.dto.AuthorRequest
import tech.provokedynamic.wdusbmidterm.model.projection.AuthorBookItem
import tech.provokedynamic.wdusbmidterm.model.projection.AuthorResponse
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import java.time.Instant

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {
    @Transactional(readOnly = true)
    fun getAllAuthors(): List<AuthorResponse> =
        authorRepository.findAllByDeletedAtNull()

    @Transactional(readOnly = true)
    fun getAuthors(pageable: Pageable): Page<AuthorResponse> =
        authorRepository.findAllByDeletedAtNull(pageable)

    @Transactional(readOnly = true)
    fun getAuthorById(id: Long): AuthorResponse =
        authorRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Author $id not found")

    @Transactional(readOnly = true)
    fun getBooksForAuthor(authorId: Long): List<AuthorBookItem> =
        bookRepository.findAllByAuthorIdAndDeletedAtNull(authorId)

    @Transactional(readOnly = true)
    fun countAuthors(): Long = authorRepository.countByDeletedAtNull()

    @Transactional
    fun createAuthor(request: AuthorRequest): AuthorResponse {
        if (authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                request.firstName.trim(), request.lastName.trim()
            )
        ) throw EntityAlreadyExistsException("Author '${request.firstName} ${request.lastName}' already exists")

        val author = Author(
            firstName = request.firstName.trim(),
            lastName = request.lastName.trim(),
        )
        author.bio = request.bio?.trim()?.ifBlank { null }

        val saved = authorRepository.save(author)

        return authorRepository.findByIdAndDeletedAtNull(saved.id)!!
    }

    @Transactional
    fun updateAuthor(id: Long, request: AuthorRequest): AuthorResponse {
        val author = authorRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Author $id not found") }

        author.deletedAt?.let {
            throw EntityDeletedException("Author $id has been deleted")
        }

        author.firstName = request.firstName.trim()
        author.lastName = request.lastName.trim()
        author.bio = request.bio?.trim()?.ifBlank { null }

        authorRepository.save(author)

        return authorRepository.findByIdAndDeletedAtNull(id)!!
    }

    @Transactional
    fun softDeleteAuthor(id: Long) {
        val author = authorRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Author $id not found") }

        author.deletedAt = Instant.now()

        authorRepository.save(author)
    }
}
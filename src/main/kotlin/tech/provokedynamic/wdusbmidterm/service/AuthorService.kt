package tech.provokedynamic.wdusbmidterm.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.dto.request.AuthorRequest
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorBookResponse
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorResponse
import tech.provokedynamic.wdusbmidterm.entity.Author
import tech.provokedynamic.wdusbmidterm.entity.toResponse
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import java.time.Instant

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {

    @Cacheable("authors:all", key = "'list'", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    fun getAllAuthors(): List<AuthorResponse> =
        authorRepository.findAllByDeletedAtNull()

    @Cacheable("authors:paged", key = "#pageable.pageNumber + ':' + #pageable.pageSize", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    fun getAuthors(pageable: Pageable): Page<AuthorResponse> =
        authorRepository.findAllByDeletedAtNull(pageable)

    @Cacheable("authors:single", key = "#id")
    @Transactional(readOnly = true)
    fun getAuthorById(id: Long): AuthorResponse =
        authorRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Author $id not found")

    @Transactional(readOnly = true)
    fun getBooksForAuthor(authorId: Long): List<AuthorBookResponse> {
        authorRepository.findByIdAndDeletedAtNull(authorId)
            ?: throw EntityNotFoundException("Author $authorId not found")
        return bookRepository.findAllByAuthorIdAndDeletedAtNull(authorId)
    }

    @Cacheable("authors:count", key = "'total'")
    @Transactional(readOnly = true)
    fun countAuthors(): Long = authorRepository.countByDeletedAtNull()

    @Caching(
        evict = [
            CacheEvict("authors:all", allEntries = true),
            CacheEvict("authors:paged", allEntries = true),
            CacheEvict("authors:count", allEntries = true)
        ]
    )
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

        return authorRepository.save(author).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict("authors:all", allEntries = true),
            CacheEvict("authors:paged", allEntries = true),
            CacheEvict("authors:single", key = "#id")
        ]
    )
    @Transactional
    fun updateAuthor(id: Long, request: AuthorRequest): AuthorResponse {
        val author = authorRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Author $id not found") }
        author.deletedAt?.let { throw EntityDeletedException("Author $id has been deleted") }

        author.firstName = request.firstName.trim()
        author.lastName = request.lastName.trim()
        author.bio = request.bio?.trim()?.ifBlank { null }

        return authorRepository.save(author).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict("authors:all", allEntries = true),
            CacheEvict("authors:paged", allEntries = true),
            CacheEvict("authors:single", key = "#id"),
            CacheEvict("authors:count", allEntries = true)
        ]
    )
    @Transactional
    fun softDeleteAuthor(id: Long) {
        val author = authorRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Author $id not found") }

        author.deletedAt?.let { throw EntityDeletedException("Author $id is already deleted") }

        author.deletedAt = Instant.now()

        authorRepository.save(author)
    }
}
package tech.provokedynamic.wdusbmidterm.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.dto.request.BookRequest
import tech.provokedynamic.wdusbmidterm.dto.response.BookCardResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookCatalogResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookDetailResponse
import tech.provokedynamic.wdusbmidterm.entity.Book
import tech.provokedynamic.wdusbmidterm.entity.toDetailResponse
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository
import java.time.Instant

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val genreRepository: GenreRepository,
    private val publisherRepository: PublisherRepository
) {

    private val log = LoggerFactory.getLogger(BookService::class.java)

    @Cacheable("books:paged", key = "#pageable.pageNumber + ':' + #pageable.pageSize", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    fun getBooks(pageable: Pageable): Page<BookCatalogResponse> =
        bookRepository.findAllByDeletedAtNull(pageable)

    @Cacheable("books:single", key = "#id")
    @Transactional(readOnly = true)
    fun getBookById(id: Long): BookDetailResponse =
        bookRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Book $id not found")

    @Cacheable("books:recent", key = "'list'", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    fun getRecentBooks(): List<BookCardResponse> =
        bookRepository.findTop6ByDeletedAtNullOrderByUpdatedAtDesc()

    @Cacheable("books:count", key = "'total'")
    @Transactional(readOnly = true)
    fun getTotalBooks(): Long = bookRepository.countByDeletedAtNull()

    @Caching(
        evict = [
            CacheEvict("books:paged", allEntries = true),
            CacheEvict("books:recent", allEntries = true),
            CacheEvict("books:count", allEntries = true)
        ]
    )
    @Transactional
    fun createBook(request: BookRequest): BookDetailResponse {
        log.debug("Creating book with ISBN '{}'", request.isbn)

        if (bookRepository.existsByIsbnAndDeletedAtNull(request.isbn.trim()))
            throw EntityAlreadyExistsException("A book with ISBN '${request.isbn}' already exists")

        val publisher = publisherRepository.findByIdAndDeletedAtNull(request.publisherId!!)
            ?: throw EntityNotFoundException("Publisher ${request.publisherId} not found")

        val book = Book(
            isbn = request.isbn.trim(),
            title = request.title.trim(),
            pageCount = request.pageCount!!,
            publicationDate = request.publicationDate!!,
            publisher = publisher
        )
        book.authors.addAll(authorRepository.findAllById(request.authorIds))
        book.genres.addAll(genreRepository.findAllById(request.genreIds))

        val saved = bookRepository.save(book).toDetailResponse()
        log.info("Book created: id={}, title='{}', isbn='{}'", saved.id, saved.title, saved.isbn)
        return saved
    }

    @Caching(
        evict = [
            CacheEvict("books:paged", allEntries = true),
            CacheEvict("books:recent", allEntries = true),
            CacheEvict("books:single", key = "#id")
        ]
    )
    @Transactional
    fun updateBook(id: Long, request: BookRequest): BookDetailResponse {
        log.debug("Updating book id={}", id)

        val book = bookRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Book $id not found") }
        book.deletedAt?.let { throw EntityDeletedException("Book $id has been deleted") }

        book.isbn = request.isbn.trim()
        book.title = request.title.trim()
        book.pageCount = request.pageCount!!
        book.publicationDate = request.publicationDate!!
        book.publisher = publisherRepository.findById(request.publisherId!!)
            .orElseThrow { EntityNotFoundException("Publisher ${request.publisherId} not found") }

        book.authors.clear()
        book.authors.addAll(authorRepository.findAllById(request.authorIds))
        book.genres.clear()
        book.genres.addAll(genreRepository.findAllById(request.genreIds))

        val saved = bookRepository.save(book).toDetailResponse()
        log.info("Book updated: id={}, title='{}'", saved.id, saved.title)
        return saved
    }

    @Caching(
        evict = [
            CacheEvict("books:paged", allEntries = true),
            CacheEvict("books:recent", allEntries = true),
            CacheEvict("books:single", key = "#id"),
            CacheEvict("books:count", allEntries = true)
        ]
    )
    @Transactional
    fun deleteBook(id: Long) {
        log.debug("Soft-deleting book id={}", id)

        val book = bookRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Book $id not found") }

        book.deletedAt?.let { throw EntityDeletedException("Book $id is already deleted") }

        book.deletedAt = Instant.now()
        bookRepository.save(book)
        log.info("Book soft-deleted: id={}", id)
    }
}

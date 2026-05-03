package tech.provokedynamic.wdusbmidterm.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.entity.Book
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.model.dto.BookCreateRequest
import tech.provokedynamic.wdusbmidterm.model.projection.BookCatalogItem
import tech.provokedynamic.wdusbmidterm.model.projection.BookDetailProjection
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
    @Transactional(readOnly = true)
    fun getCatalog(pageable: Pageable): Page<BookCatalogItem> =
        bookRepository.findAllByDeletedAtNull(pageable)

    @Transactional(readOnly = true)
    fun getBookById(id: Long): BookDetailProjection =
        bookRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Book $id not found")

    @Transactional
    fun createBook(request: BookCreateRequest): BookDetailProjection {
        if (bookRepository.existsByIsbnAndDeletedAtNull(request.isbn.trim()))
            throw EntityAlreadyExistsException("A book with ISBN '${request.isbn}' already exists")

        val publisher = publisherRepository.findByIdAndDeletedAtNull(request.publisherId)
            ?: throw EntityNotFoundException("Publisher ${request.publisherId} not found")

        val authors = authorRepository.findAllById(request.authorIds)
        val genres = genreRepository.findAllById(request.genreIds)

        val book = Book(
            isbn = request.isbn.trim(),
            title = request.title.trim(),
            pageCount = request.pageCount,
            publicationDate = request.publicationDate,
            publisher = publisher
        )
        book.authors.addAll(authors)
        book.genres.addAll(genres)

        val saved = bookRepository.save(book)

        return bookRepository.findByIdAndDeletedAtNull(saved.id)!!
    }

    @Transactional
    fun updateBook(id: Long, request: BookCreateRequest): BookDetailProjection {
        val book = bookRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Book $id not found") }

        book.deletedAt?.let { throw EntityDeletedException("Book $id has been deleted") }

        book.isbn = request.isbn.trim()
        book.title = request.title.trim()
        book.pageCount = request.pageCount
        book.publicationDate = request.publicationDate
        book.publisher = publisherRepository.findById(request.publisherId)
            .orElseThrow { EntityNotFoundException("Publisher ${request.publisherId} not found") }

        book.authors.clear()
        book.authors.addAll(authorRepository.findAllById(request.authorIds))

        book.genres.clear()
        book.genres.addAll(genreRepository.findAllById(request.genreIds))

        bookRepository.save(book)
        return bookRepository.findByIdAndDeletedAtNull(id)!!
    }

    @Transactional
    fun softDeleteBook(id: Long) {
        val book = bookRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Book $id not found") }
        book.deletedAt = Instant.now()
        bookRepository.save(book)
    }
}
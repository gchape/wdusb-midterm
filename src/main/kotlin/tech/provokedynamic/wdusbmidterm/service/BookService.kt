package tech.provokedynamic.wdusbmidterm.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.model.dto.BookRequestDTO
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDTO
import tech.provokedynamic.wdusbmidterm.model.dto.toResponseDto
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
    fun getCatalog(pageable: Pageable): Page<BookResponseDTO> =
        bookRepository.findAllByDeletedAtNull(pageable).map { it.toResponseDto() }

    @Transactional(readOnly = true)
    fun getBookById(id: Long): BookResponseDTO {
        val book = bookRepository.findById(id)
            .orElseThrow { NoSuchElementException("Book $id not found") }
        if (book.deletedAt != null) throw IllegalArgumentException("Book is deleted")
        return book.toResponseDto()
    }

    @Transactional
    fun createBook(request: BookRequestDTO): BookResponseDTO {
        val publisher = publisherRepository.findById(request.publisherId!!)
            .orElseThrow { NoSuchElementException("Publisher not found") }
        val authors = authorRepository.findAllById(request.authorIds)
        val genres = genreRepository.findAllById(request.genreIds)

        val book = tech.provokedynamic.wdusbmidterm.entity.Book(
            isbn = request.isbn.trim(),
            title = request.title.trim(),
            pageCount = request.pageCount,
            publicationDate = request.publicationDate,
            publisher = publisher
        )
        book.authors.addAll(authors)
        book.genres.addAll(genres)

        return bookRepository.save(book).toResponseDto()
    }

    @Transactional
    fun updateBook(id: Long, request: BookRequestDTO): BookResponseDTO {
        val book = bookRepository.findById(id)
            .orElseThrow { NoSuchElementException("Book $id not found") }
        if (book.deletedAt != null) throw IllegalArgumentException("Book is deleted")

        book.isbn = request.isbn.trim()
        book.title = request.title.trim()
        book.pageCount = request.pageCount
        book.publicationDate = request.publicationDate
        book.publisher = publisherRepository.findById(request.publisherId!!)
            .orElseThrow { NoSuchElementException("Publisher not found") }

        book.authors.clear()
        book.authors.addAll(authorRepository.findAllById(request.authorIds))

        book.genres.clear()
        book.genres.addAll(genreRepository.findAllById(request.genreIds))

        return bookRepository.save(book).toResponseDto()
    }

    @Transactional
    fun softDeleteBook(id: Long) {
        val book = bookRepository.findById(id)
            .orElseThrow { NoSuchElementException("Book $id not found") }
        book.deletedAt = Instant.now()
        bookRepository.save(book)
    }
}
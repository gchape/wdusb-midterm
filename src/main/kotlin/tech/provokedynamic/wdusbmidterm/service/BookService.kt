package tech.provokedynamic.wdusbmidterm.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.entity.Book
import tech.provokedynamic.wdusbmidterm.model.dto.BookRequestDto
import tech.provokedynamic.wdusbmidterm.model.dto.BookResponseDto
import tech.provokedynamic.wdusbmidterm.model.dto.toResponseDto
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository

@Service
class BookService(
    val bookRepository: BookRepository,
    val authorRepository: AuthorRepository,
) {

    @Transactional(readOnly = true)
    fun getRecentBooks(pageable: Pageable): List<BookResponseDto> =
        bookRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable)
            .map { it.toResponseDto() }

    @Transactional(readOnly = true)
    fun getBookByIsbn(isbn: String): BookResponseDto? =
        bookRepository.findByIsbnAndDeletedFalse(isbn)?.toResponseDto()

    @Transactional(readOnly = true)
    fun getAllBooks(pageable: Pageable): Page<BookResponseDto> =
        bookRepository.findAllByDeletedFalse(pageable)
            .map { it.toResponseDto() }

    fun getBooksByGenre(genre: String, pageable: Pageable): Page<BookResponseDto> =
        bookRepository.findByGenreIgnoreCaseAndDeletedFalse(genre, pageable)
            .map { it.toResponseDto() }

    @Transactional(readOnly = true)
    fun searchBooks(query: String, pageable: Pageable): Page<BookResponseDto> =
        bookRepository.search(query, pageable)
            .map { it.toResponseDto() }

    @Transactional
    fun createBook(request: BookRequestDto): BookResponseDto {
        val authors = authorRepository.findAllById(request.authorIds).toMutableSet()
        val book = Book(
            title = request.title,
            isbn = request.isbn,
            publishedAt = request.publishedAt,
            pageCount = request.pageCount,
        ).apply {
            genre = request.genre
            this.authors = authors
        }
        return bookRepository.save(book).toResponseDto()
    }
}
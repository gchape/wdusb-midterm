package tech.provokedynamic.wdusbmidterm.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import tech.provokedynamic.wdusbmidterm.dto.request.BookRequest
import tech.provokedynamic.wdusbmidterm.dto.response.BookDetailResponse
import tech.provokedynamic.wdusbmidterm.entity.Book
import tech.provokedynamic.wdusbmidterm.entity.Publisher
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository
import java.time.Instant
import java.time.LocalDate
import java.util.*

@DisplayName("BookService")
class BookServiceTest {

    private lateinit var bookRepository: BookRepository
    private lateinit var authorRepository: AuthorRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var publisherRepository: PublisherRepository
    private lateinit var bookService: BookService

    @BeforeEach
    fun setUp() {
        bookRepository = mock(BookRepository::class.java)
        authorRepository = mock(AuthorRepository::class.java)
        genreRepository = mock(GenreRepository::class.java)
        publisherRepository = mock(PublisherRepository::class.java)
        bookService = BookService(bookRepository, authorRepository, genreRepository, publisherRepository)
    }

    private fun makePublisher(id: Long = 1L): Publisher {
        val p = Publisher("Tor Books")
        val f = Publisher::class.java.getDeclaredField("id")
        f.isAccessible = true
        f.set(p, id)
        return p
    }

    private fun makeBook(id: Long = 1L): Book {
        val b = Book("Dune", "9780441013593", makePublisher(), LocalDate.of(1965, 8, 1), 412)
        val f = Book::class.java.getDeclaredField("id")
        f.isAccessible = true
        f.set(b, id)
        return b
    }

    private fun validRequest() = BookRequest(
        title = "Dune",
        isbn = "9780441013593",
        publisherId = 1L,
        publicationDate = LocalDate.of(1965, 8, 1),
        pageCount = 412,
        genreIds = listOf(1L),
        authorIds = listOf(1L)
    )

    @Nested
    @DisplayName("createBook")
    inner class CreateBook {

        @Test
        @DisplayName("creates book successfully when ISBN is unique")
        fun createBook_success() {
            `when`(bookRepository.existsByIsbnAndDeletedAtNull("9780441013593")).thenReturn(false)
            `when`(publisherRepository.findByIdAndDeletedAtNull(1L)).thenReturn(makePublisher())
            `when`(authorRepository.findAllById(listOf(1L))).thenReturn(emptyList())
            `when`(genreRepository.findAllById(listOf(1L))).thenReturn(emptyList())
            val book = makeBook()
            `when`(bookRepository.save(any())).thenReturn(book)

            val result = bookService.createBook(validRequest())

            assertNotNull(result)
            verify(bookRepository).save(any())
        }

        @Test
        @DisplayName("throws EntityAlreadyExistsException for duplicate ISBN")
        fun createBook_duplicateIsbn() {
            `when`(bookRepository.existsByIsbnAndDeletedAtNull("9780441013593")).thenReturn(true)

            assertThrows<EntityAlreadyExistsException> {
                bookService.createBook(validRequest())
            }
            verify(bookRepository, never()).save(any())
        }

        @Test
        @DisplayName("throws EntityNotFoundException when publisher not found")
        fun createBook_publisherNotFound() {
            `when`(bookRepository.existsByIsbnAndDeletedAtNull(anyString())).thenReturn(false)
            `when`(publisherRepository.findByIdAndDeletedAtNull(1L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                bookService.createBook(validRequest())
            }
        }
    }

    @Nested
    @DisplayName("deleteBook")
    inner class DeleteBook {

        @Test
        @DisplayName("soft-deletes book by setting deletedAt")
        fun deleteBook_success() {
            val book = makeBook()
            `when`(bookRepository.findById(1L)).thenReturn(Optional.of(book))
            `when`(bookRepository.save(any())).thenReturn(book)

            bookService.deleteBook(1L)

            assertNotNull(book.deletedAt)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when book does not exist")
        fun deleteBook_notFound() {
            `when`(bookRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<EntityNotFoundException> {
                bookService.deleteBook(99L)
            }
        }

        @Test
        @DisplayName("throws EntityDeletedException when book is already deleted")
        fun deleteBook_alreadyDeleted() {
            val book = makeBook()
            book.deletedAt = Instant.now()
            `when`(bookRepository.findById(1L)).thenReturn(Optional.of(book))

            assertThrows<EntityDeletedException> {
                bookService.deleteBook(1L)
            }
        }
    }

    @Nested
    @DisplayName("getBookById")
    inner class GetBookById {

        @Test
        @DisplayName("throws EntityNotFoundException when book not found")
        fun getById_notFound() {
            `when`(bookRepository.findByIdAndDeletedAtNull(99L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                bookService.getBookById(99L)
            }
        }

        @Test
        @DisplayName("returns BookDetailResponse when found")
        fun getById_found() {
            val response = mock(BookDetailResponse::class.java)
            `when`(response.id).thenReturn(1L)
            `when`(bookRepository.findByIdAndDeletedAtNull(1L)).thenReturn(response)

            val result = bookService.getBookById(1L)

            assertEquals(1L, result.id)
        }
    }

    @Test
    @DisplayName("getTotalBooks returns count from repository")
    fun getTotalBooks() {
        `when`(bookRepository.countByDeletedAtNull()).thenReturn(30L)

        assertEquals(30L, bookService.getTotalBooks())
    }
}
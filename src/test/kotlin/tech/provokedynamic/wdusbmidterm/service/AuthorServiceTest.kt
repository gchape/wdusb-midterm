package tech.provokedynamic.wdusbmidterm.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import tech.provokedynamic.wdusbmidterm.dto.request.AuthorRequest
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorResponse
import tech.provokedynamic.wdusbmidterm.entity.Author
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import java.time.Instant
import java.util.*

@DisplayName("AuthorService")
class AuthorServiceTest {

    private lateinit var authorRepository: AuthorRepository
    private lateinit var bookRepository: BookRepository
    private lateinit var authorService: AuthorService

    @BeforeEach
    fun setUp() {
        authorRepository = mock(AuthorRepository::class.java)
        bookRepository = mock(BookRepository::class.java)
        authorService = AuthorService(authorRepository, bookRepository)
    }

    private fun makeAuthor(id: Long = 1L, firstName: String = "George", lastName: String = "Orwell"): Author {
        val a = Author(firstName, lastName)
        val idField = Author::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(a, id)
        return a
    }

    private fun makeAuthorResponse(
        id: Long = 1L,
        firstName: String = "George",
        lastName: String = "Orwell"
    ): AuthorResponse =
        object : AuthorResponse {
            override val id = id
            override val firstName = firstName
            override val lastName = lastName
            override val bio: String? = null
        }

    // ── createAuthor ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createAuthor")
    inner class CreateAuthor {

        @Test
        @DisplayName("saves and returns new author when name is unique")
        fun createAuthor_success() {
            val request = AuthorRequest("George", "Orwell", null)
            `when`(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    "George",
                    "Orwell"
                )
            ).thenReturn(false)
            val saved = makeAuthor()
            `when`(authorRepository.save(any())).thenReturn(saved)

            val result = authorService.createAuthor(request)

            assertEquals("George", result.firstName)
            assertEquals("Orwell", result.lastName)
            verify(authorRepository).save(any())
        }

        @Test
        @DisplayName("throws EntityAlreadyExistsException when author already exists")
        fun createAuthor_duplicate() {
            val request = AuthorRequest("George", "Orwell", null)
            `when`(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    "George",
                    "Orwell"
                )
            ).thenReturn(true)

            assertThrows<EntityAlreadyExistsException> {
                authorService.createAuthor(request)
            }
            verify(authorRepository, never()).save(any())
        }

        @Test
        @DisplayName("trims whitespace from names before saving")
        fun createAuthor_trimsWhitespace() {
            val request = AuthorRequest("  George  ", "  Orwell  ", null)
            `when`(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    "George",
                    "Orwell"
                )
            ).thenReturn(false)
            val saved = makeAuthor()
            `when`(authorRepository.save(any())).thenReturn(saved)

            authorService.createAuthor(request)

            verify(authorRepository).existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                "George",
                "Orwell"
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["  ", "\t", ""])
        @DisplayName("stores null bio when bio is blank")
        fun createAuthor_blankBioStoredAsNull(bio: String) {
            val request = AuthorRequest("George", "Orwell", bio)
            `when`(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    anyString(), anyString()
                )
            ).thenReturn(false)
            val saved = makeAuthor()
            `when`(authorRepository.save(any())).thenAnswer { invocation ->
                val author = invocation.getArgument<Author>(0)
                assertNull(author.bio)
                saved
            }

            authorService.createAuthor(request)
        }
    }

    // ── updateAuthor ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateAuthor")
    inner class UpdateAuthor {

        @Test
        @DisplayName("updates and returns author when found and not deleted")
        fun updateAuthor_success() {
            val author = makeAuthor()
            `when`(authorRepository.findById(1L)).thenReturn(Optional.of(author))
            `when`(authorRepository.save(any())).thenReturn(author)

            val result = authorService.updateAuthor(1L, AuthorRequest("Isaac", "Asimov", "Bio"))

            assertEquals("Isaac", result.firstName)
            assertEquals("Asimov", result.lastName)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when author does not exist")
        fun updateAuthor_notFound() {
            `when`(authorRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<EntityNotFoundException> {
                authorService.updateAuthor(99L, AuthorRequest("X", "Y", null))
            }
        }

        @Test
        @DisplayName("throws EntityDeletedException when author is soft-deleted")
        fun updateAuthor_deleted() {
            val author = makeAuthor()
            author.deletedAt = Instant.now()
            `when`(authorRepository.findById(1L)).thenReturn(Optional.of(author))

            assertThrows<EntityDeletedException> {
                authorService.updateAuthor(1L, AuthorRequest("X", "Y", null))
            }
        }
    }

    // ── deleteAuthor ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteAuthor")
    inner class DeleteAuthor {

        @Test
        @DisplayName("soft-deletes author by setting deletedAt")
        fun deleteAuthor_success() {
            val author = makeAuthor()
            `when`(authorRepository.findById(1L)).thenReturn(Optional.of(author))
            `when`(authorRepository.save(any())).thenReturn(author)

            authorService.deleteAuthor(1L)

            assertNotNull(author.deletedAt)
            verify(authorRepository).save(author)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when author does not exist")
        fun deleteAuthor_notFound() {
            `when`(authorRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<EntityNotFoundException> {
                authorService.deleteAuthor(99L)
            }
        }

        @Test
        @DisplayName("throws EntityDeletedException when author is already deleted")
        fun deleteAuthor_alreadyDeleted() {
            val author = makeAuthor()
            author.deletedAt = Instant.now()
            `when`(authorRepository.findById(1L)).thenReturn(Optional.of(author))

            assertThrows<EntityDeletedException> {
                authorService.deleteAuthor(1L)
            }
        }
    }

    // ── getAuthorById ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAuthorById")
    inner class GetAuthorById {

        @Test
        @DisplayName("returns author when found")
        fun getById_found() {
            val response = makeAuthorResponse()
            `when`(authorRepository.findByIdAndDeletedAtNull(1L)).thenReturn(response)

            val result = authorService.getAuthorById(1L)

            assertEquals(1L, result.id)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when not found")
        fun getById_notFound() {
            `when`(authorRepository.findByIdAndDeletedAtNull(99L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                authorService.getAuthorById(99L)
            }
        }
    }

    // ── getTotalAuthors ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getTotalAuthors returns count from repository")
    fun getTotalAuthors_returnsCount() {
        `when`(authorRepository.countByDeletedAtNull()).thenReturn(42L)

        assertEquals(42L, authorService.getTotalAuthors())
    }

    // ── getAuthors (paged) ───────────────────────────────────────────────────

    @Test
    @DisplayName("getAuthors returns page from repository")
    fun getAuthors_returnsPaged() {
        val response = makeAuthorResponse()
        val page = PageImpl(listOf(response))
        val pageable = PageRequest.of(0, 10)
        `when`(authorRepository.findAllByDeletedAtNull(pageable)).thenReturn(page)

        val result = authorService.getAuthors(pageable)

        assertEquals(1, result.totalElements)
    }
}

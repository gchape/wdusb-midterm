package tech.provokedynamic.wdusbmidterm.repository

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.config.TestCacheConfig
import tech.provokedynamic.wdusbmidterm.entity.Author
import java.time.Instant

@DataJpaTest
@ActiveProfiles("dev")
@Transactional
@Import(TestCacheConfig::class)
@DisplayName("AuthorRepository")
class AuthorRepositoryTest {

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeEach
    fun cleanUp() {
        authorRepository.deleteAllInBatch()
        entityManager.createNativeQuery("ALTER TABLE public.authors ALTER COLUMN id RESTART WITH 1000")
            .executeUpdate()
    }

    private fun saveAuthor(
        firstName: String = "George",
        lastName: String = "Orwell",
        deleted: Boolean = false
    ): Author {
        val a = Author(firstName, lastName)
        if (deleted) a.deletedAt = Instant.now()
        return authorRepository.save(a)
    }

    @Nested
    @DisplayName("findAllByDeletedAtNull (list)")
    inner class FindAllByDeletedAtNull {

        @Test
        @DisplayName("returns only non-deleted authors")
        fun findAll_excludesDeleted() {
            saveAuthor("George", "Orwell")
            saveAuthor("Aldous", "Huxley", deleted = true)

            val result = authorRepository.findAllByDeletedAtNull()

            assertEquals(1, result.size)
            assertEquals("Orwell", result[0].lastName)
        }

        @Test
        @DisplayName("returns empty list when all authors are deleted")
        fun findAll_allDeleted() {
            saveAuthor(deleted = true)

            val result = authorRepository.findAllByDeletedAtNull()

            assertTrue(result.isEmpty())
        }
    }

    @Nested
    @DisplayName("findAllByDeletedAtNull (paged)")
    inner class FindAllByDeletedAtNullPaged {

        @Test
        @DisplayName("returns paginated non-deleted authors")
        fun findAllPaged_excludesDeleted() {
            saveAuthor("George", "Orwell")
            saveAuthor("Aldous", "Huxley")
            saveAuthor("Isaac", "Asimov", deleted = true)

            val pageable = PageRequest.of(0, 10, Sort.by("lastName"))
            val result = authorRepository.findAllByDeletedAtNull(pageable)

            assertEquals(2, result.totalElements)
        }

        @Test
        @DisplayName("respects page size")
        fun findAllPaged_respectsPageSize() {
            repeat(5) { saveAuthor("Author$it", "LastName$it") }

            val pageable = PageRequest.of(0, 2, Sort.by("lastName"))
            val result = authorRepository.findAllByDeletedAtNull(pageable)

            assertEquals(2, result.content.size)
            assertEquals(5, result.totalElements)
            assertEquals(3, result.totalPages)
        }
    }

    @Nested
    @DisplayName("findByIdAndDeletedAtNull")
    inner class FindByIdAndDeletedAtNull {

        @Test
        @DisplayName("returns author when active")
        fun findById_found() {
            val saved = saveAuthor()

            val result = authorRepository.findByIdAndDeletedAtNull(saved.id)

            assertNotNull(result)
            assertEquals("George", result!!.firstName)
        }

        @Test
        @DisplayName("returns null when author is soft-deleted")
        fun findById_deletedReturnsNull() {
            val saved = saveAuthor(deleted = true)

            val result = authorRepository.findByIdAndDeletedAtNull(saved.id)

            assertNull(result)
        }

        @Test
        @DisplayName("returns null when author does not exist")
        fun findById_notFound() {
            val result = authorRepository.findByIdAndDeletedAtNull(Long.MAX_VALUE)
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull")
    inner class ExistsByName {

        @Test
        @DisplayName("returns true when active author with same name exists")
        fun exists_true() {
            saveAuthor("George", "Orwell")

            assertTrue(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    "george", "orwell"
                )
            )
        }

        @Test
        @DisplayName("returns false when author is deleted")
        fun exists_deletedReturnsFalse() {
            saveAuthor("George", "Orwell", deleted = true)

            assertFalse(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    "George", "Orwell"
                )
            )
        }

        @Test
        @DisplayName("returns false when author does not exist")
        fun exists_false() {
            assertFalse(
                authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(
                    "Nobody", "Here"
                )
            )
        }
    }

    @Nested
    @DisplayName("countByDeletedAtNull")
    inner class CountByDeletedAtNull {

        @Test
        @DisplayName("returns only count of non-deleted authors")
        fun count_excludesDeleted() {
            saveAuthor("George", "Orwell")
            saveAuthor("Aldous", "Huxley")
            saveAuthor("Isaac", "Asimov", deleted = true)

            assertEquals(2, authorRepository.countByDeletedAtNull())
        }

        @Test
        @DisplayName("returns 0 when no authors exist")
        fun count_zero() {
            assertEquals(0, authorRepository.countByDeletedAtNull())
        }
    }
}

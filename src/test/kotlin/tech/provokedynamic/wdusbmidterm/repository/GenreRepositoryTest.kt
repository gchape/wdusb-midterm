package tech.provokedynamic.wdusbmidterm.repository

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.config.TestCacheConfig
import tech.provokedynamic.wdusbmidterm.entity.Genre

@DataJpaTest
@ActiveProfiles("dev")
@Transactional
@Import(TestCacheConfig::class)
@DisplayName("GenreRepository")
class GenreRepositoryTest {

    @Autowired
    lateinit var genreRepository: GenreRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeEach
    fun cleanUp() {
        genreRepository.deleteAllInBatch()
        entityManager.createNativeQuery("ALTER TABLE public.genres ALTER COLUMN id RESTART WITH 1000")
            .executeUpdate()
    }

    private fun saveGenre(name: String): Genre = genreRepository.save(Genre(name))

    @Nested
    @DisplayName("existsByNameIgnoreCase")
    inner class ExistsByNameIgnoreCase {

        @ParameterizedTest
        @ValueSource(strings = ["Science Fiction", "SCIENCE FICTION", "science fiction", "Science fiction"])
        @DisplayName("returns true for any case variant of existing name")
        fun existsByName_caseInsensitive(variant: String) {
            saveGenre("Science Fiction")

            assertTrue(genreRepository.existsByNameIgnoreCase(variant))
        }

        @Test
        @DisplayName("returns false when genre does not exist")
        fun existsByName_false() {
            assertFalse(genreRepository.existsByNameIgnoreCase("NonExistentGenre"))
        }
    }

    @Nested
    @DisplayName("findAllByOrderByNameAsc")
    inner class FindAllByOrderByNameAsc {

        @Test
        @DisplayName("returns genres in ascending alphabetical order")
        fun findAll_sortedAscending() {
            saveGenre("Science Fiction")
            saveGenre("Fantasy")
            saveGenre("Cyberpunk")

            val result = genreRepository.findAllByOrderByNameAsc()

            assertEquals(3, result.size)
            assertEquals("Cyberpunk", result[0].name)
            assertEquals("Fantasy", result[1].name)
            assertEquals("Science Fiction", result[2].name)
        }

        @Test
        @DisplayName("returns empty list when no genres exist")
        fun findAll_empty() {
            val result = genreRepository.findAllByOrderByNameAsc()
            assertTrue(result.isEmpty())
        }
    }

    @Nested
    @DisplayName("findGenreById")
    inner class FindGenreById {

        @Test
        @DisplayName("returns GenreResponse projection when genre exists")
        fun findById_found() {
            val saved = saveGenre("Epic Fantasy")

            val result = genreRepository.findGenreById(saved.id)

            assertNotNull(result)
            assertEquals("Epic Fantasy", result!!.name)
            assertEquals(saved.id, result.id)
        }

        @Test
        @DisplayName("returns null when genre does not exist")
        fun findById_notFound() {
            val result = genreRepository.findGenreById(Long.MAX_VALUE)
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("save and delete")
    inner class SaveAndDelete {

        @Test
        @DisplayName("persists new genre with auto-generated id")
        fun save_persistsGenre() {
            val genre = genreRepository.save(Genre("Dystopian"))

            assertTrue(genre.id > 0)
            assertEquals("Dystopian", genre.name)
        }

        @Test
        @DisplayName("deleteById removes the genre")
        fun deleteById_removesGenre() {
            val saved = saveGenre("Space Opera")

            genreRepository.deleteById(saved.id)

            assertFalse(genreRepository.existsById(saved.id))
        }
    }
}

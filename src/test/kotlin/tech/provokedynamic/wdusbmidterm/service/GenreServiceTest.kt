package tech.provokedynamic.wdusbmidterm.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import tech.provokedynamic.wdusbmidterm.dto.request.GenreRequest
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse
import tech.provokedynamic.wdusbmidterm.entity.Genre
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository
import java.util.*

@DisplayName("GenreService")
class GenreServiceTest {

    private lateinit var genreRepository: GenreRepository
    private lateinit var genreService: GenreService

    @BeforeEach
    fun setUp() {
        genreRepository = mock(GenreRepository::class.java)
        genreService = GenreService(genreRepository)
    }

    private fun makeGenre(id: Long = 1L, name: String = "Science Fiction"): Genre {
        val g = Genre(name)
        val idField = Genre::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(g, id)
        return g
    }

    private fun makeGenreResponse(id: Long = 1L, name: String = "Science Fiction"): GenreResponse =
        object : GenreResponse {
            override val id = id
            override val name = name
        }

    @Nested
    @DisplayName("createGenre")
    inner class CreateGenre {

        @Test
        @DisplayName("creates and returns genre when name is unique")
        fun createGenre_success() {
            `when`(genreRepository.existsByNameIgnoreCase("Science Fiction")).thenReturn(false)
            val saved = makeGenre()
            `when`(genreRepository.save(any())).thenReturn(saved)

            val result = genreService.createGenre(GenreRequest("Science Fiction"))

            assertEquals("Science Fiction", result.name)
        }

        @Test
        @DisplayName("throws EntityAlreadyExistsException for duplicate name")
        fun createGenre_duplicate() {
            `when`(genreRepository.existsByNameIgnoreCase("Fantasy")).thenReturn(true)

            assertThrows<EntityAlreadyExistsException> {
                genreService.createGenre(GenreRequest("Fantasy"))
            }
            verify(genreRepository, never()).save(any())
        }

        @Test
        @DisplayName("trims name before checking and saving")
        fun createGenre_trimsName() {
            `when`(genreRepository.existsByNameIgnoreCase("Fantasy")).thenReturn(false)
            val saved = makeGenre(name = "Fantasy")
            `when`(genreRepository.save(any())).thenReturn(saved)

            genreService.createGenre(GenreRequest("  Fantasy  "))

            verify(genreRepository).existsByNameIgnoreCase("Fantasy")
        }
    }

    @Nested
    @DisplayName("getGenreById")
    inner class GetGenreById {

        @Test
        @DisplayName("returns genre when found")
        fun getById_found() {
            `when`(genreRepository.findGenreById(1L)).thenReturn(makeGenreResponse())

            val result = genreService.getGenreById(1L)

            assertEquals(1L, result.id)
            assertEquals("Science Fiction", result.name)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when not found")
        fun getById_notFound() {
            `when`(genreRepository.findGenreById(99L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                genreService.getGenreById(99L)
            }
        }
    }

    @Nested
    @DisplayName("updateGenre")
    inner class UpdateGenre {

        @Test
        @DisplayName("updates genre name and returns updated response")
        fun updateGenre_success() {
            val genre = makeGenre()
            `when`(genreRepository.findById(1L)).thenReturn(Optional.of(genre))
            `when`(genreRepository.save(genre)).thenReturn(genre)

            val result = genreService.updateGenre(1L, GenreRequest("Cyberpunk"))

            assertEquals("Cyberpunk", result.name)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when genre does not exist")
        fun updateGenre_notFound() {
            `when`(genreRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<EntityNotFoundException> {
                genreService.updateGenre(99L, GenreRequest("Anything"))
            }
        }
    }

    @Nested
    @DisplayName("deleteGenre")
    inner class DeleteGenre {

        @Test
        @DisplayName("deletes genre by id when it exists")
        fun deleteGenre_success() {
            `when`(genreRepository.existsById(1L)).thenReturn(true)

            genreService.deleteGenre(1L)

            verify(genreRepository).deleteById(1L)
        }

        @Test
        @DisplayName("throws EntityNotFoundException when genre does not exist")
        fun deleteGenre_notFound() {
            `when`(genreRepository.existsById(99L)).thenReturn(false)

            assertThrows<EntityNotFoundException> {
                genreService.deleteGenre(99L)
            }
            verify(genreRepository, never()).deleteById(any())
        }
    }

    @Test
    @DisplayName("getAllGenres returns list from repository")
    fun getAllGenres_returnsList() {
        val genres = listOf(makeGenreResponse(1L, "Sci-Fi"), makeGenreResponse(2L, "Fantasy"))
        `when`(genreRepository.findAllByOrderByNameAsc()).thenReturn(genres)

        val result = genreService.getAllGenres()

        assertEquals(2, result.size)
        assertEquals("Sci-Fi", result[0].name)
    }
}

package tech.provokedynamic.wdusbmidterm.controller.api

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tech.provokedynamic.wdusbmidterm.config.ActuatorConfig
import tech.provokedynamic.wdusbmidterm.config.AppProperties
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.exception.GlobalExceptionHandler
import tech.provokedynamic.wdusbmidterm.repository.*
import tech.provokedynamic.wdusbmidterm.security.SecurityConfig
import tech.provokedynamic.wdusbmidterm.service.GenreService
import tech.provokedynamic.wdusbmidterm.service.UserDetailsService

@WebMvcTest(
    controllers = [GenreRestController::class],
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [ActuatorConfig::class]
    )]
)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
@DisplayName("GenreRestController")
class GenreRestControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var genreService: GenreService

    @MockitoBean
    lateinit var userDetailsService: UserDetailsService

    @MockitoBean
    lateinit var appProperties: AppProperties

    @MockitoBean
    lateinit var bookRepository: BookRepository

    @MockitoBean
    lateinit var authorRepository: AuthorRepository

    @MockitoBean
    lateinit var genreRepository: GenreRepository

    @MockitoBean
    lateinit var publisherRepository: PublisherRepository

    @MockitoBean
    lateinit var userRepository: UserRepository

    @MockitoBean
    lateinit var cacheManager: CacheManager

    private fun genreResponse(id: Long = 1L, name: String = "Science Fiction"): GenreResponse =
        object : GenreResponse {
            override val id = id
            override val name = name
        }

    @Nested
    @DisplayName("GET /api/genres")
    inner class GetAll {

        @Test
        @DisplayName("returns 200 with list of genres")
        fun getAll_returnsOk() {
            `when`(genreService.getAllGenres()).thenReturn(
                listOf(genreResponse(1L, "Science Fiction"), genreResponse(2L, "Fantasy"))
            )

            mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Science Fiction"))
                .andExpect(jsonPath("$[1].name").value("Fantasy"))
        }

        @Test
        @DisplayName("returns 200 with empty list when no genres exist")
        fun getAll_emptyList() {
            `when`(genreService.getAllGenres()).thenReturn(emptyList())

            mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(0))
        }
    }

    @Nested
    @DisplayName("GET /api/genres/{id}")
    inner class GetById {

        @Test
        @DisplayName("returns 200 when genre exists")
        fun getById_found() {
            `when`(genreService.getGenreById(1L)).thenReturn(genreResponse())

            mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Science Fiction"))
        }

        @Test
        @DisplayName("returns 404 when genre not found")
        fun getById_notFound() {
            `when`(genreService.getGenreById(99L))
                .thenThrow(EntityNotFoundException("Genre 99 not found"))

            mockMvc.perform(get("/api/genres/99"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.status").value(404))
        }
    }

    @Nested
    @DisplayName("POST /api/genres")
    inner class Create {

        @Test
        @DisplayName("returns 201 when admin creates genre")
        fun create_success() {
            `when`(genreService.createGenre(any())).thenReturn(genreResponse())

            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Science Fiction"}""")
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.name").value("Science Fiction"))
        }

        @Test
        @DisplayName("returns 422 when name is blank")
        fun create_blankName() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":""}""")
            )
                .andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("$.fields.name").exists())
        }

        @Test
        @DisplayName("returns 409 when genre name already exists")
        fun create_conflict() {
            `when`(genreService.createGenre(any()))
                .thenThrow(EntityAlreadyExistsException("Genre already exists"))

            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Fantasy"}""")
            )
                .andExpect(status().isConflict)
        }

        @Test
        @DisplayName("returns 403 when non-admin tries to create genre")
        fun create_forbidden() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Horror"}""")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        @DisplayName("returns 401 when unauthenticated")
        fun create_unauthorized() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Horror"}""")
            )
                .andExpect(status().isUnauthorized)
        }
    }

    @Nested
    @DisplayName("DELETE /api/genres/{id}")
    inner class Delete {

        @Test
        @DisplayName("returns 204 when admin deletes existing genre")
        fun delete_success() {
            doNothing().`when`(genreService).deleteGenre(1L)

            mockMvc.perform(
                delete("/api/genres/1")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            )
                .andExpect(status().isNoContent)
        }

        @Test
        @DisplayName("returns 404 when genre does not exist")
        fun delete_notFound() {
            `when`(genreService.deleteGenre(99L))
                .thenThrow(EntityNotFoundException("Genre 99 not found"))

            mockMvc.perform(
                delete("/api/genres/99")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            )
                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("returns 403 for non-admin user")
        fun delete_forbidden() {
            mockMvc.perform(
                delete("/api/genres/1")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
            )
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("PUT /api/genres/{id}")
    inner class Update {

        @Test
        @DisplayName("returns 200 with updated genre")
        fun update_success() {
            `when`(genreService.updateGenre(eq(1L), any()))
                .thenReturn(genreResponse(1L, "Cyberpunk"))

            mockMvc.perform(
                put("/api/genres/1")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Cyberpunk"}""")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value("Cyberpunk"))
        }

        @Test
        @DisplayName("returns 404 when genre to update does not exist")
        fun update_notFound() {
            `when`(genreService.updateGenre(eq(99L), any()))
                .thenThrow(EntityNotFoundException("Genre 99 not found"))

            mockMvc.perform(
                put("/api/genres/99")
                    .with(csrf())
                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Cyberpunk"}""")
            )
                .andExpect(status().isNotFound)
        }
    }
}

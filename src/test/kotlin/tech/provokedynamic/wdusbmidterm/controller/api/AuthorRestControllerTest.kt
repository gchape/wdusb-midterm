package tech.provokedynamic.wdusbmidterm.controller.api

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tech.provokedynamic.wdusbmidterm.config.ActuatorConfig
import tech.provokedynamic.wdusbmidterm.config.AppProperties
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorResponse
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.exception.GlobalExceptionHandler
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository
import tech.provokedynamic.wdusbmidterm.repository.UserRepository
import tech.provokedynamic.wdusbmidterm.security.SecurityConfig
import tech.provokedynamic.wdusbmidterm.service.AuthorService
import tech.provokedynamic.wdusbmidterm.service.UserDetailsService

@WebMvcTest(
    controllers = [AuthorRestController::class],
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = [ActuatorConfig::class]
    )]
)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
@DisplayName("AuthorRestController")
class AuthorRestControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var authorService: AuthorService

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

    private fun authorResponse(id: Long = 1L, first: String = "George", last: String = "Orwell"): AuthorResponse =
        object : AuthorResponse {
            override val id = id
            override val firstName = first
            override val lastName = last
            override val bio: String? = null
        }

    @Nested
    @DisplayName("GET /api/authors/{id}")
    inner class GetById {

        @Test
        @DisplayName("returns 200 with author when found")
        fun getById_found() {
            `when`(authorService.getAuthorById(1L)).thenReturn(authorResponse())

            mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("George"))
                .andExpect(jsonPath("$.lastName").value("Orwell"))
        }

        @Test
        @DisplayName("returns 404 when author not found")
        fun getById_notFound() {
            `when`(authorService.getAuthorById(99L))
                .thenThrow(EntityNotFoundException("Author 99 not found"))

            mockMvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
        }
    }

    @Nested
    @DisplayName("POST /api/authors")
    inner class Create {

        @Test
        @DisplayName("returns 201 when admin creates author")
        fun create_success() {
            `when`(authorService.createAuthor(any())).thenReturn(authorResponse())

            mockMvc.perform(
                post("/api/authors")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"George","lastName":"Orwell"}""")
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.firstName").value("George"))
        }

        @Test
        @DisplayName("returns 422 when firstName is blank")
        fun create_validationError() {
            mockMvc.perform(
                post("/api/authors")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"","lastName":"Orwell"}""")
            )
                .andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("$.fields.firstName").exists())
        }

        @Test
        @DisplayName("returns 422 when lastName is missing")
        fun create_missingLastName() {
            mockMvc.perform(
                post("/api/authors")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"George"}""")
            )
                .andExpect(status().isUnprocessableEntity)
        }

        @Test
        @DisplayName("returns 409 when author already exists")
        fun create_conflict() {
            `when`(authorService.createAuthor(any()))
                .thenThrow(EntityAlreadyExistsException("Author already exists"))

            mockMvc.perform(
                post("/api/authors")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"George","lastName":"Orwell"}""")
            )
                .andExpect(status().isConflict)
        }

        @Test
        @DisplayName("returns 403 for non-admin user")
        fun create_forbidden() {
            mockMvc.perform(
                post("/api/authors")
                    .with(user("regularuser").roles("USER"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"George","lastName":"Orwell"}""")
            )
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("DELETE /api/authors/{id}")
    inner class Delete {

        @Test
        @DisplayName("returns 204 on successful soft-delete")
        fun delete_success() {
            doNothing().`when`(authorService).deleteAuthor(1L)

            mockMvc.perform(
                delete("/api/authors/1")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
            )
                .andExpect(status().isNoContent)
        }

        @Test
        @DisplayName("returns 404 when author to delete does not exist")
        fun delete_notFound() {
            `when`(authorService.deleteAuthor(99L))
                .thenThrow(EntityNotFoundException("Author 99 not found"))

            mockMvc.perform(
                delete("/api/authors/99")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
            )
                .andExpect(status().isNotFound)
        }
    }
}

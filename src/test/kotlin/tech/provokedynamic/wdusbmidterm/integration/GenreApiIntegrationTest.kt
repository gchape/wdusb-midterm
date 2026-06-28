package tech.provokedynamic.wdusbmidterm.integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import tech.provokedynamic.wdusbmidterm.config.TestCacheConfig

@SpringBootTest
@ActiveProfiles("dev")
@Import(TestCacheConfig::class)
@DisplayName("Genre API Integration Tests")
class GenreApiIntegrationTest {

    @Autowired
    lateinit var wac: WebApplicationContext
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    private fun wipeGenres() {
        jdbcTemplate.execute("DELETE FROM public.book_genres")
        jdbcTemplate.execute("DELETE FROM public.genres")
        jdbcTemplate.execute("ALTER TABLE public.genres ALTER COLUMN id RESTART WITH 1000")
    }

    private fun restoreFullSeed() {
        wipeGenres()
        jdbcTemplate.execute(
            """
            INSERT INTO public.genres (id, name) VALUES
              (1,  'Science Fiction'),
              (2,  'Fantasy'),
              (3,  'Epic Fantasy'),
              (9,  'Dystopian'),
              (10, 'Cyberpunk'),
              (11, 'Space Opera'),
              (12, 'Hard Science Fiction'),
              (13, 'Speculative Fiction'),
              (14, 'Alternate History'),
              (15, 'Post-Apocalyptic')
        """.trimIndent()
        )
    }

    private fun wipAndReseedOne() {
        wipeGenres()
        jdbcTemplate.execute("INSERT INTO public.genres (id, name) VALUES (1, 'Science Fiction')")
    }

    // ── Read endpoints (public) ──────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/genres")
    inner class GetAllGenres {

        @BeforeEach
        fun seed() = restoreFullSeed()

        @Test
        @DisplayName("returns 200 with seeded genres")
        fun getAll_returnsSeededData() {
            mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(10))
        }

        @Test
        @DisplayName("genres are sorted alphabetically by name")
        fun getAll_sortedByName() {
            mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].name").value("Alternate History"))
        }
    }

    @Nested
    @DisplayName("GET /api/genres/{id}")
    inner class GetGenreById {

        @BeforeEach
        fun seed() = restoreFullSeed()

        @Test
        @DisplayName("returns 200 with genre for seeded id=1")
        fun getById_found() {
            mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Science Fiction"))
        }

        @Test
        @DisplayName("returns 404 for non-existent genre")
        fun getById_notFound() {
            mockMvc.perform(get("/api/genres/99999"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isNotEmpty)
        }
    }

    // ── Write endpoints (ADMIN only) ─────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/genres")
    inner class CreateGenre {

        @BeforeEach
        fun resetForWrites() = wipAndReseedOne()

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("creates genre and returns 201")
        fun create_success() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Solarpunk"}""")
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.name").value("Solarpunk"))
                .andExpect(jsonPath("$.id").isNumber)
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns 409 when genre name is a duplicate (seeded)")
        fun create_duplicateSeeded() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Science Fiction"}""")
            )
                .andExpect(status().isConflict)
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns 422 when name is blank")
        fun create_blankName() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":""}""")
            )
                .andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("$.fields.name").exists())
        }

        @Test
        @WithMockUser(roles = ["USER"])
        @DisplayName("returns 403 for USER role")
        fun create_forbiddenForUser() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Biopunk"}""")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        @DisplayName("returns 401 for unauthenticated request")
        fun create_unauthorized() {
            mockMvc.perform(
                post("/api/genres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Biopunk"}""")
            )
                .andExpect(status().isUnauthorized)
        }
    }

    @Nested
    @DisplayName("DELETE /api/genres/{id}")
    inner class DeleteGenre {

        @BeforeEach
        fun resetForWrites() = wipAndReseedOne()

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns 204 for seeded genre id=1")
        fun delete_success() {
            mockMvc.perform(delete("/api/genres/1").with(csrf()))
                .andExpect(status().isNoContent)

            mockMvc.perform(get("/api/genres/1"))
                .andExpect(status().isNotFound)
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns 404 for non-existent genre")
        fun delete_notFound() {
            mockMvc.perform(delete("/api/genres/99999").with(csrf()))
                .andExpect(status().isNotFound)
        }
    }
}

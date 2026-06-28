package tech.provokedynamic.wdusbmidterm.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

/**
 * Full integration tests: boots the complete application context (H2 + Flyway + Security).
 * Uses seeded data from V2__seed_data_h2.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Genre API Integration Tests")
class GenreApiIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    // ── Read endpoints (public) ──────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/genres")
    inner class GetAllGenres {

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

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns 204 for seeded genre id=1")
        fun delete_success() {
            mockMvc.perform(delete("/api/genres/1").with(csrf()))
                .andExpect(status().isNoContent)

            // Verify it's gone
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

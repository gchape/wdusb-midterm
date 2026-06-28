package tech.provokedynamic.wdusbmidterm.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Author API Integration Tests")
class AuthorApiIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Nested
    @DisplayName("GET /api/authors")
    inner class GetAuthors {

        @Test
        @DisplayName("returns 200 with paged authors from seed data")
        fun getAll_returnsPage() {
            mockMvc.perform(get("/api/authors?page=1&size=10"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content").isArray)
                .andExpect(jsonPath("$.totalElements").value(20))
        }

        @Test
        @DisplayName("page 2 with size 5 returns correct slice")
        fun getAll_pagination() {
            mockMvc.perform(get("/api/authors?page=2&size=5"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalPages").value(4))
        }
    }

    @Nested
    @DisplayName("GET /api/authors/{id}")
    inner class GetAuthorById {

        @Test
        @DisplayName("returns seeded author id=8 (George Orwell)")
        fun getById_found() {
            mockMvc.perform(get("/api/authors/8"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.firstName").value("George"))
                .andExpect(jsonPath("$.lastName").value("Orwell"))
        }

        @Test
        @DisplayName("returns 404 for unknown id")
        fun getById_notFound() {
            mockMvc.perform(get("/api/authors/99999"))
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    @DisplayName("GET /api/authors/{id}/books")
    inner class GetAuthorBooks {

        @Test
        @DisplayName("returns books for seeded author (Orwell wrote 1984)")
        fun getBooks_returnsBooks() {
            mockMvc.perform(get("/api/authors/8/books"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("1984"))
        }
    }

    @Nested
    @DisplayName("POST /api/authors")
    inner class CreateAuthor {

        @Test
        @DisplayName("creates new author and returns 409 (already seeded)")
        fun create_success() {
            mockMvc.perform(
                post("/api/authors")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"Ursula K.","lastName":"Le Guin","bio":"American author"}""")
            )
                .andExpect(status().isConflict)
        }

        @Test
        @DisplayName("creates genuinely new author and returns 201")
        fun create_newAuthor() {
            mockMvc.perform(
                post("/api/authors")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"Toni","lastName":"Morrison","bio":"American novelist"}""")
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.firstName").value("Toni"))
                .andExpect(jsonPath("$.lastName").value("Morrison"))
                .andExpect(jsonPath("$.id").isNumber)
        }

        @Test
        @DisplayName("returns 422 when firstName is too short")
        fun create_firstNameTooShort() {
            mockMvc.perform(
                post("/api/authors")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"A","lastName":"Morrison"}""")
            )
                .andExpect(status().isUnprocessableEntity)
                .andExpect(jsonPath("$.fields.firstName").exists())
        }

        @Test
        @DisplayName("returns 403 for USER role")
        fun create_forbidden() {
            mockMvc.perform(
                post("/api/authors")
                    .with(csrf())
                    .with(user("regularuser").roles("USER"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"Toni","lastName":"Morrison"}""")
            )
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("PUT /api/authors/{id}")
    inner class UpdateAuthor {

        @Test
        @DisplayName("updates seeded author and returns 200")
        fun update_success() {
            mockMvc.perform(
                put("/api/authors/8")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"Eric","lastName":"Blair","bio":"Pen name: George Orwell"}""")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.firstName").value("Eric"))
                .andExpect(jsonPath("$.lastName").value("Blair"))
        }

        @Test
        @DisplayName("returns 404 when updating non-existent author")
        fun update_notFound() {
            mockMvc.perform(
                put("/api/authors/99999")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"firstName":"Nobody","lastName":"Here"}""")
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    @DisplayName("DELETE /api/authors/{id}")
    inner class DeleteAuthor {

        @Test
        @DisplayName("soft-deletes seeded author and returns 204")
        fun delete_success() {
            mockMvc.perform(
                delete("/api/authors/8")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
            )
                .andExpect(status().isNoContent)

            mockMvc.perform(get("/api/authors/8"))
                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("returns 404 for unknown author")
        fun delete_notFound() {
            mockMvc.perform(
                delete("/api/authors/99999")
                    .with(csrf())
                    .with(user("admin").roles("ADMIN"))
            )
                .andExpect(status().isNotFound)
        }
    }
}

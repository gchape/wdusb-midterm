package tech.provokedynamic.wdusbmidterm.integration

import org.junit.jupiter.api.DisplayName
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("I18n Integration Tests")
class I18nIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("404 message is in English by default")
    fun notFound_english() {
        mockMvc.perform(get("/api/authors/99999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("The requested resource was not found"))
    }

    @Test
    @DisplayName("404 message is in Georgian when Accept-Language: ka")
    fun notFound_georgian() {
        mockMvc.perform(
            get("/api/authors/99999")
                .header("Accept-Language", "ka")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("მოთხოვნილი რესურსი ვერ მოიძებნა"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("validation error messages are in Georgian when Accept-Language: ka")
    fun validation_georgian() {
        mockMvc.perform(
            post("/api/authors")
                .with(csrf())
                .header("Accept-Language", "ka")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"","lastName":"Test"}""")
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(jsonPath("$.fields.firstName").value("სახელი არ შეიძლება იყოს ცარიელი"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("validation error messages are in English by default")
    fun validation_english() {
        mockMvc.perform(
            post("/api/authors")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"firstName":"","lastName":"Test"}""")
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(jsonPath("$.fields.firstName").value("First name cannot be blank"))
    }
}

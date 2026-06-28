package tech.provokedynamic.wdusbmidterm.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Actuator Integration Tests")
class ActuatorIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Nested
    @DisplayName("GET /actuator/health")
    inner class Health {

        @Test
        @DisplayName("returns 200 and UP status (public endpoint)")
        fun health_returnsUp() {
            mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("UP"))
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns component details for authenticated ADMIN")
        fun health_adminSeesDetails() {
            mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists())
        }

        @Test
        @DisplayName("catalog health component is UP with seeded data")
        fun health_catalogComponentUp() {
            mockMvc.perform(get("/actuator/health/catalogHealthIndicator"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("UP"))
        }
    }

    @Nested
    @DisplayName("GET /actuator/info")
    inner class Info {

        @Test
        @DisplayName("returns 200 with app info (public endpoint)")
        fun info_returnsOk() {
            mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.app.title").value("Library System [DEV]"))
                .andExpect(jsonPath("$.app.organization").value("WDUSB University Library"))
                .andExpect(jsonPath("$.features.swagger").value(true))
        }
    }

    @Nested
    @DisplayName("GET /actuator/metrics")
    inner class Metrics {

        @Test
        @DisplayName("returns 401 for unauthenticated request")
        fun metrics_unauthorized() {
            mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized)
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns 200 for ADMIN")
        fun metrics_adminAllowed() {
            mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.names").isArray)
        }

        @Test
        @WithMockUser(roles = ["USER"])
        @DisplayName("returns 403 for USER role")
        fun metrics_userForbidden() {
            mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden)
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("custom library.books.active.total gauge is registered")
        fun metrics_customBooksGauge() {
            mockMvc.perform(get("/actuator/metrics/library.books.active.total"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value("library.books.active.total"))
                .andExpect(jsonPath("$.measurements[0].value").isNumber)
        }

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("custom library.authors.active.total gauge is registered")
        fun metrics_customAuthorsGauge() {
            mockMvc.perform(get("/actuator/metrics/library.authors.active.total"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value("library.authors.active.total"))
        }
    }

    @Nested
    @DisplayName("GET /actuator/caches")
    inner class Caches {

        @Test
        @WithMockUser(roles = ["ADMIN"])
        @DisplayName("returns cache information for ADMIN")
        fun caches_adminAllowed() {
            mockMvc.perform(get("/actuator/caches"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.cacheManagers").exists())
        }
    }
}

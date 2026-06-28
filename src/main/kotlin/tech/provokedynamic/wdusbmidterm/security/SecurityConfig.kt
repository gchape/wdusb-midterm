package tech.provokedynamic.wdusbmidterm.security

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import tech.provokedynamic.wdusbmidterm.service.UserDetailsService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userDetailsService: UserDetailsService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider =
        DaoAuthenticationProvider(userDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/api/**", "/actuator/**")
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                auth.requestMatchers(
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/v3/api-docs"
                ).permitAll()
                auth.requestMatchers(HttpMethod.GET, "/", "/books", "/books/{id}",
                    "/authors", "/authors/{id}").permitAll()
                auth.requestMatchers("/login", "/register").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                auth.requestMatchers("/actuator/health", "/actuator/info").permitAll()
                auth.requestMatchers("/actuator/**").hasRole("ADMIN")
                auth.requestMatchers(
                    "/books/new", "/books/*/edit", "/books/*/delete",
                    "/authors/add", "/authors/*/edit", "/authors/*/delete",
                    "/admin/**"
                ).hasRole("ADMIN")
                auth.anyRequest().authenticated()
            }
            .httpBasic { }  // ← required for @WithMockUser
            .formLogin { login ->
                login
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { request, response, _ ->
                    if (request.requestURI.startsWith("/api/") || request.requestURI.startsWith("/actuator/")) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                    } else {
                        response.sendRedirect(request.contextPath + "/login")
                    }
                }
                ex.accessDeniedHandler { request, response, _ ->
                    if (request.requestURI.startsWith("/api/") || request.requestURI.startsWith("/actuator/")) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
                    } else {
                        response.sendRedirect(request.contextPath + "/login?error=forbidden")
                    }
                }
            }
        return http.build()
    }
}
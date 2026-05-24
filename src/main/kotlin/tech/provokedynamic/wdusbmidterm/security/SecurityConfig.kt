package tech.provokedynamic.wdusbmidterm.security

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
            // Disable CSRF only for the stateless REST API (/api/**).
            // Browser-facing MVC endpoints keep CSRF protection enabled.
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/api/**")
            }

            .authorizeHttpRequests { auth ->
                // Static assets
                auth.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                // Swagger / OpenAPI docs
                auth.requestMatchers(
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/v3/api-docs"
                ).permitAll()

                // Public read-only MVC pages
                auth.requestMatchers(HttpMethod.GET, "/", "/books", "/books/{id}",
                    "/authors", "/authors/{id}").permitAll()

                // Auth pages — /auth/register removed (AuthController maps only /register)
                auth.requestMatchers("/login", "/register").permitAll()

                // REST API: GET is public; mutating operations require ADMIN role
                auth.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                // MVC mutating routes (GET + POST) require ADMIN
                auth.requestMatchers(
                    "/books/new", "/books/*/edit", "/books/*/delete",
                    "/authors/add", "/authors/*/edit", "/authors/*/delete",
                    "/admin/**"
                ).hasRole("ADMIN")

                auth.anyRequest().authenticated()
            }

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

        return http.build()
    }
}
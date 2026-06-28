package tech.provokedynamic.wdusbmidterm.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import tech.provokedynamic.wdusbmidterm.dto.request.RegisterRequest
import tech.provokedynamic.wdusbmidterm.entity.User
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.model.Role
import tech.provokedynamic.wdusbmidterm.repository.UserRepository
import java.util.*

@DisplayName("UserService")
class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: BCryptPasswordEncoder
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        passwordEncoder = mock(BCryptPasswordEncoder::class.java)
        userService = UserService(userRepository, passwordEncoder)
    }

    private fun makeUser(id: Long = 1L, username: String = "testuser"): User =
        User(username = username, passwordHash = "hashed", role = Role.USER)

    @Nested
    @DisplayName("register")
    inner class Register {

        @Test
        @DisplayName("registers user successfully when username is available")
        fun register_success() {
            val request = RegisterRequest("newuser", "password123", "password123")
            `when`(userRepository.existsByUsername("newuser")).thenReturn(false)
            `when`(passwordEncoder.encode("password123")).thenReturn("\$2a\$10\$hash")
            val user = makeUser(username = "newuser")
            `when`(userRepository.save(any())).thenReturn(user)

            val result = userService.register(request)

            assertEquals("newuser", result.username)
            assertEquals(Role.USER, result.role)
            verify(userRepository).save(any())
        }

        @Test
        @DisplayName("throws EntityAlreadyExistsException when username is taken")
        fun register_usernameTaken() {
            `when`(userRepository.existsByUsername("existinguser")).thenReturn(true)

            assertThrows<EntityAlreadyExistsException> {
                userService.register(RegisterRequest("existinguser", "pass123", "pass123"))
            }
            verify(userRepository, never()).save(any())
        }

        @Test
        @DisplayName("trims username before checking availability")
        fun register_trimsUsername() {
            `when`(userRepository.existsByUsername("newuser")).thenReturn(false)
            `when`(passwordEncoder.encode(anyString())).thenReturn("hash")
            val user = makeUser(username = "newuser")
            `when`(userRepository.save(any())).thenReturn(user)

            userService.register(RegisterRequest("  newuser  ", "pass123", "pass123"))

            verify(userRepository).existsByUsername("newuser")
        }

        @Test
        @DisplayName("encodes password before saving")
        fun register_encodesPassword() {
            `when`(userRepository.existsByUsername(anyString())).thenReturn(false)
            `when`(passwordEncoder.encode("mypassword")).thenReturn("encoded_hash")
            val user = makeUser()
            `when`(userRepository.save(any())).thenReturn(user)

            userService.register(RegisterRequest("user", "mypassword", "mypassword"))

            verify(passwordEncoder).encode("mypassword")
        }
    }

    @Nested
    @DisplayName("setUserEnabled")
    inner class SetUserEnabled {

        @Test
        @DisplayName("disables user when enabled=false")
        fun setEnabled_disables() {
            val user = makeUser()
            `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
            `when`(userRepository.save(user)).thenReturn(user)

            val result = userService.setUserEnabled(1L, false)

            assertFalse(result.enabled)
        }

        @Test
        @DisplayName("enables user when enabled=true")
        fun setEnabled_enables() {
            val user = makeUser().also { it.enabled = false }
            `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
            `when`(userRepository.save(user)).thenReturn(user)

            val result = userService.setUserEnabled(1L, true)

            assertTrue(result.enabled)
        }

        @Test
        @DisplayName("throws NoSuchElementException when user does not exist")
        fun setEnabled_notFound() {
            `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<NoSuchElementException> {
                userService.setUserEnabled(99L, false)
            }
        }
    }

    @Nested
    @DisplayName("promoteToAdmin")
    inner class PromoteToAdmin {

        @Test
        @DisplayName("promotes user role to ADMIN")
        fun promoteToAdmin_success() {
            val user = makeUser()
            assertEquals(Role.USER, user.role)
            `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
            `when`(userRepository.save(user)).thenReturn(user)

            val result = userService.promoteToAdmin(1L)

            assertEquals(Role.ADMIN, result.role)
        }

        @Test
        @DisplayName("throws NoSuchElementException when user does not exist")
        fun promoteToAdmin_notFound() {
            `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<NoSuchElementException> {
                userService.promoteToAdmin(99L)
            }
        }
    }
}

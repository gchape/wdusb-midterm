package tech.provokedynamic.wdusbmidterm.service

import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.dto.request.RegisterRequest
import tech.provokedynamic.wdusbmidterm.entity.User
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.model.Role
import tech.provokedynamic.wdusbmidterm.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun register(request: RegisterRequest): User {
        log.debug("Attempting to register user '{}'", request.username)

        if (userRepository.existsByUsername(request.username.trim()))
            throw EntityAlreadyExistsException("Username '${request.username}' is already taken")

        val user = User(
            username = request.username.trim(),
            passwordHash = passwordEncoder.encode(request.password)!!,
            role = Role.USER
        )
        val saved = userRepository.save(user)
        log.info("User '{}' registered successfully", saved.username)
        return saved
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> {
        log.debug("Fetching all users")
        return userRepository.findAll()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun setUserEnabled(id: Long, enabled: Boolean): User {
        log.info("Setting enabled={} for user id={}", enabled, id)
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User $id not found") }
        user.enabled = enabled
        return userRepository.save(user)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun promoteToAdmin(id: Long): User {
        log.info("Promoting user id={} to ADMIN", id)
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User $id not found") }
        user.role = Role.ADMIN
        return userRepository.save(user)
    }
}

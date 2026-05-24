package tech.provokedynamic.wdusbmidterm.service

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.dto.request.RegisterRequest
import tech.provokedynamic.wdusbmidterm.entity.User
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.model.view.Role
import tech.provokedynamic.wdusbmidterm.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(request: RegisterRequest): User {
        if (userRepository.existsByUsername(request.username.trim()))
            throw EntityAlreadyExistsException("Username '${request.username}' is already taken")

        val user = User(
            username = request.username.trim(),
            passwordHash = passwordEncoder.encode(request.password)!!,
            role = Role.USER
        )
        return userRepository.save(user)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> = userRepository.findAll()

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun setUserEnabled(id: Long, enabled: Boolean): User {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User $id not found") }
        user.enabled = enabled
        return userRepository.save(user)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun promoteToAdmin(id: Long): User {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User $id not found") }
        user.role = Role.ADMIN
        return userRepository.save(user)
    }
}

package tech.provokedynamic.wdusbmidterm.service

import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import tech.provokedynamic.wdusbmidterm.repository.UserRepository

@Service
class UserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    private val log = LoggerFactory.getLogger(UserDetailsService::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        log.debug("Loading user by username '{}'", username)

        val appUser = userRepository.findByUsername(username)
            ?: run {
                log.warn("Authentication failed: user '{}' not found", username)
                throw UsernameNotFoundException("User '$username' not found")
            }

        log.debug("User '{}' found with role={}, enabled={}", appUser.username, appUser.role, appUser.enabled)

        return User.builder()
            .username(appUser.username)
            .password(appUser.passwordHash)
            .authorities(SimpleGrantedAuthority("ROLE_${appUser.role.name}"))
            .disabled(!appUser.enabled)
            .build()
    }
}

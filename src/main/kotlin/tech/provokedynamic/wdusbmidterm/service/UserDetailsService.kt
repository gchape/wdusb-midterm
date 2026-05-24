package tech.provokedynamic.wdusbmidterm.service

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

    override fun loadUserByUsername(username: String): UserDetails {
        val appUser = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User '$username' not found")

        return User.builder()
            .username(appUser.username)
            .password(appUser.passwordHash)
            .authorities(SimpleGrantedAuthority("ROLE_${appUser.role.name}"))
            .disabled(!appUser.enabled)
            .build()
    }
}

package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Author
import tech.provokedynamic.wdusbmidterm.model.projection.AuthorResponse

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {
    fun existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(firstName: String, lastName: String): Boolean
    fun findAllByDeletedAtNull(pageable: Pageable): Page<AuthorResponse>
    fun findAllByDeletedAtNull(): List<AuthorResponse>
    fun findByIdAndDeletedAtNull(id: Long): AuthorResponse?
    fun countByDeletedAtNull(): Long
}
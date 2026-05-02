package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Author

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {
    fun findAllByDeletedAtNull(pageable: Pageable): Page<Author>

    fun findAllByDeletedAtNull(): List<Author>

    fun countByDeletedAtNull(): Long
}
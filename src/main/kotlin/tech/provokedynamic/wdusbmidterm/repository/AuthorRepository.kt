package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.jpa.repository.JpaRepository
import tech.provokedynamic.wdusbmidterm.entity.Author

interface AuthorRepository : JpaRepository<Author, Int> {
    fun findAllByDeletedFalse(): List<Author>
}
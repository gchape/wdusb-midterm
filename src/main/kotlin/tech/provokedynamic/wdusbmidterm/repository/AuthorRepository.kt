package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.cache.annotation.CachePut
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorResponse
import tech.provokedynamic.wdusbmidterm.entity.Author

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {

    @CachePut("authors:exists", key = "#firstName + ':' + #lastName")
    fun existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDeletedAtNull(firstName: String, lastName: String): Boolean

    @CachePut("authors:paged", key = "#pageable.pageNumber + ':' + #pageable.pageSize", unless = "#result.isEmpty()")
    fun findAllByDeletedAtNull(pageable: Pageable): Page<AuthorResponse>

    @CachePut("authors:all", key = "'list'", unless = "#result.isEmpty()")
    fun findAllByDeletedAtNull(): List<AuthorResponse>

    @CachePut("authors:single", key = "#id", unless = "#result == null")
    fun findByIdAndDeletedAtNull(id: Long): AuthorResponse?

    @CachePut("authors:count", key = "'total'")
    fun countByDeletedAtNull(): Long
}
package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Book

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun findAllByDeletedAtNull(pageable: Pageable): Page<Book>

    fun findTop6ByDeletedAtNullOrderByIdDesc(): List<Book>

    fun countByDeletedAtNull(): Long

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId AND b.deletedAt IS NULL")
    fun findAllByAuthorId(@Param("authorId") authorId: Long): List<Book>
}
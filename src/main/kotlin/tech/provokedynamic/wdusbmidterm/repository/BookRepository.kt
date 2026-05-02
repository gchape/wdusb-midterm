package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import tech.provokedynamic.wdusbmidterm.entity.Book

interface BookRepository : JpaRepository<Book, Int> {
    fun findByIsbnAndDeletedFalse(isbn: String): Book?

    fun findByDeletedFalseOrderByCreatedAtDesc(pageable: Pageable): List<Book>

    fun findAllByDeletedFalse(pageable: Pageable): Page<Book>

    fun findByGenreIgnoreCaseAndDeletedFalse(genre: String, pageable: Pageable): Page<Book>

    @Query(
        """
            SELECT b FROM Book b 
            WHERE b.deleted = false
                AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) 
                OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%')) 
                OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :query, '%')))
         """
    )
    fun search(query: String, pageable: Pageable): Page<Book>
}
package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.cache.annotation.CachePut
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorBookResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookCardResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookCatalogResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookDetailResponse
import tech.provokedynamic.wdusbmidterm.entity.Book

@Repository
interface BookRepository : JpaRepository<Book, Long> {

    @CachePut("books:exists", key = "#isbn")
    fun existsByIsbnAndDeletedAtNull(isbn: String): Boolean

    @EntityGraph(attributePaths = ["genres"])
    @CachePut("books:paged", key = "#pageable.pageNumber + ':' + #pageable.pageSize", unless = "#result.isEmpty()")
    fun findAllByDeletedAtNull(pageable: Pageable): Page<BookCatalogResponse>

    @CachePut("books:recent", key = "'list'", unless = "#result.isEmpty()")
    fun findTop6ByDeletedAtNullOrderByUpdatedAtDesc(): List<BookCardResponse>

    @EntityGraph(attributePaths = ["authors", "genres", "publisher"])
    @CachePut("books:single", key = "#id", unless = "#result == null")
    fun findByIdAndDeletedAtNull(id: Long): BookDetailResponse?

    @CachePut("books:count", key = "'total'")
    fun countByDeletedAtNull(): Long

    @EntityGraph(attributePaths = ["genres"])
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId AND b.deletedAt IS NULL")
    fun findAllByAuthorIdAndDeletedAtNull(@Param("authorId") authorId: Long): List<AuthorBookResponse>
}
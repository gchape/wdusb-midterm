package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Book
import tech.provokedynamic.wdusbmidterm.model.projection.AuthorBookItem
import tech.provokedynamic.wdusbmidterm.model.projection.BookCardProjection
import tech.provokedynamic.wdusbmidterm.model.projection.BookCatalogItem
import tech.provokedynamic.wdusbmidterm.model.projection.BookDetailProjection

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun existsByIsbnAndDeletedAtNull(isbn: String): Boolean

    @EntityGraph(attributePaths = ["genres"])
    fun findAllByDeletedAtNull(pageable: Pageable): Page<BookCatalogItem>
    fun findTop6ByDeletedAtNullOrderByIdDesc(): List<BookCardProjection>

    @EntityGraph(attributePaths = ["authors", "genres", "publisher"])
    fun findByIdAndDeletedAtNull(id: Long): BookDetailProjection?
    fun countByDeletedAtNull(): Long

    @EntityGraph(attributePaths = ["genres"])
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId AND b.deletedAt IS NULL")
    fun findAllByAuthorIdAndDeletedAtNull(@Param("authorId") authorId: Long): List<AuthorBookItem>
}
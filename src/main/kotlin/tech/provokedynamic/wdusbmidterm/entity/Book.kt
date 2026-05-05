package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.SourceType
import tech.provokedynamic.wdusbmidterm.dto.response.AuthorResponse
import tech.provokedynamic.wdusbmidterm.dto.response.BookDetailResponse
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse
import tech.provokedynamic.wdusbmidterm.dto.response.PublisherResponse
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "books", schema = "public")
open class Book(
    @NotNull
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    var title: String,

    @NotNull
    @Size(max = 13)
    @Column(name = "isbn", length = 13)
    var isbn: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @org.hibernate.annotations.OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "publisher_id")
    var publisher: Publisher,

    @NotNull
    @Column(name = "publication_date")
    var publicationDate: LocalDate,

    @NotNull
    @Column(name = "page_count")
    var pageCount: Short
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "books_id_gen")
    @SequenceGenerator(name = "books_id_gen", sequenceName = "books_id_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Long = 0
        protected set

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "book_genres",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "genre_id")]
    )
    var genres: MutableSet<Genre> = mutableSetOf()

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "book_authors",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    var authors: MutableSet<Author> = mutableSetOf()

    @NotNull
    @Column(name = "created_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant = Instant.now()
        protected set

    @NotNull
    @Column(name = "updated_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var updatedAt: Instant = Instant.now()
        protected set

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
}

fun Book.toDetailResponse(): BookDetailResponse = object : BookDetailResponse {
    override val id: Long
        get() = this@toDetailResponse.id
    override val isbn: String
        get() = this@toDetailResponse.isbn
    override val title: String
        get() = this@toDetailResponse.title
    override val pageCount: Short
        get() = this@toDetailResponse.pageCount
    override val publicationDate: LocalDate
        get() = this@toDetailResponse.publicationDate
    override val publisher: PublisherResponse
        get() = this@toDetailResponse.publisher.toResponse()
    override val authors: Set<AuthorResponse>
        get() = this@toDetailResponse.authors.map { it.toResponse() }.toSet()
    override val genres: Set<GenreResponse>
        get() = this@toDetailResponse.genres.map { it.toResponse() }.toSet()
}
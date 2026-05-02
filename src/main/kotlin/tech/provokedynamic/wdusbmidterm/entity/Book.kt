package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import org.hibernate.generator.EventType
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "books")
open class Book(
    @NotNull
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    var title: String,

    @NotNull
    @Size(max = 20)
    @Column(name = "isbn", length = 20, unique = true)
    var isbn: String,

    @NotNull
    @Column(name = "published_at")
    var publishedAt: LocalDate,

    @NotNull
    @Column(name = "page_count")
    var pageCount: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int = 0
        protected set

    @ManyToMany(
        fetch = FetchType.LAZY,
    )
    @JoinTable(
        name = "authors_books",
        joinColumns = [JoinColumn(name = "book_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "author_id", referencedColumnName = "id")]
    )
    var authors: MutableSet<Author> = mutableSetOf()

    @Size(max = 50)
    @Column(name = "genre", length = 50)
    var genre: String? = null

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant? = null
        protected set

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    @org.hibernate.annotations.UpdateTimestamp(source = SourceType.DB)
    var updatedAt: Instant? = null
        protected set

    @NotNull
    @Column(name = "deleted")
    @org.hibernate.annotations.Generated(event = [EventType.INSERT])
    var deleted: Boolean = false
}
package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import java.time.Instant

@Entity
@Table(name = "authors", schema = "public")
open class Author(
    @NotNull
    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    var firstName: String,

    @NotNull
    @Size(max = 60)
    @Column(name = "last_name", length = 60)
    var lastName: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authors_id_gen")
    @SequenceGenerator(name = "authors_id_gen", sequenceName = "authors_id_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Long = 0
        protected set

    @Column(name = "bio", length = Integer.MAX_VALUE)
    var bio: String? = null

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    var books: MutableSet<Book> = mutableSetOf()

    @NotNull
    @Column(name = "created_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant = Instant.now()
        protected set

    @NotNull
    @Column(name = "updated_at", insertable = false, updatable = false)
    @org.hibernate.annotations.UpdateTimestamp(source = SourceType.DB)
    var updatedAt: Instant = Instant.now()
        protected set

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
}
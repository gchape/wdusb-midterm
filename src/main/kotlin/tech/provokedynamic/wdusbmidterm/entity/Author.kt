package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import org.hibernate.generator.EventType
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "authors")
open class Author(
    @NotNull
    @Size(max = 50)
    @Column(name = "firstname", length = 50)
    var firstname: String,

    @NotNull
    @Size(max = 60)
    @Column(name = "lastname", length = 60)
    var lastname: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "email", unique = true)
    var email: String,

    @NotNull
    @Column(name = "dob")
    var dob: LocalDate
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int = 0
        protected set

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    var books: MutableSet<Book> = mutableSetOf()

    @Column(name = "bio", length = Integer.MAX_VALUE)
    var bio: String? = null

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
package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import org.hibernate.annotations.SourceType
import org.jetbrains.annotations.NotNull
import java.time.Instant

@Entity
@Table(name = "reviews")
open class Review(
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    var book: Book,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    var user: User,

    @NotNull
    @Column(name = "rating")
    var rating: Short,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int = 0
        protected set

    @Column(name = "body", length = Integer.MAX_VALUE)
    var body: String? = null

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant? = null
        protected set
}
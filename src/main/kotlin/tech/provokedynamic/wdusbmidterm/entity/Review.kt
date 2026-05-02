package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "reviews")
open class Review {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.NotNull
@jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, optional = false)
@jakarta.persistence.JoinColumn(name = "book_id", nullable = false)
open var book: tech.provokedynamic.wdusbmidterm.entity.Book? = null
@jakarta.persistence.Column(name = "rating")
open var rating: Short? = null
@jakarta.persistence.Column(name = "body", length = Integer.MAX_VALUE)
open var body: String? = null
@org.hibernate.annotations.ColumnDefault("now()")
@jakarta.persistence.Column(name = "created_at")
open var createdAt: java.time.Instant? = null

}
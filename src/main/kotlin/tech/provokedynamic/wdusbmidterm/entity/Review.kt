package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "reviews", schema = "public")
open class Review {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "reviews_id_gen")
@jakarta.persistence.SequenceGenerator(name = "reviews_id_gen", sequenceName = "reviews_id_seq", allocationSize = 1)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.NotNull
@jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, optional = false)
@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
@jakarta.persistence.JoinColumn(name = "book_id", nullable = false)
open var book: tech.provokedynamic.wdusbmidterm.entity.Book? = null
@jakarta.validation.constraints.NotNull
@jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY, optional = false)
@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
@jakarta.persistence.JoinColumn(name = "user_id", nullable = false)
open var user: tech.provokedynamic.wdusbmidterm.entity.User? = null
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "rating", nullable = false)
open var rating: Short = 0
@jakarta.persistence.Column(name = "body", length = Integer.MAX_VALUE)
open var body: String? = null
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "created_at", nullable = false)
open var createdAt: java.time.Instant = java.time.Instant.now()
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "updated_at", nullable = false)
open var updatedAt: java.time.Instant = java.time.Instant.now()

}
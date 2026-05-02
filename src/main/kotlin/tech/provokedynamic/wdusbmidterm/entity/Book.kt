package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "books")
open class Book {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 255)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "title", nullable = false)
open var title: String = ""
@jakarta.validation.constraints.Size(max = 20)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "isbn", nullable = false, length = 20)
open var isbn: String = ""
@jakarta.persistence.Column(name = "published_at")
open var publishedAt: java.time.LocalDate? = null
@jakarta.validation.constraints.Size(max = 50)
@jakarta.persistence.Column(name = "genre", length = 50)
open var genre: String? = null
@jakarta.persistence.Column(name = "page_count")
open var pageCount: Int? = null
@org.hibernate.annotations.ColumnDefault("now()")
@jakarta.persistence.Column(name = "created_at")
open var createdAt: java.time.Instant? = null
@org.hibernate.annotations.ColumnDefault("now()")
@jakarta.persistence.Column(name = "updated_at")
open var updatedAt: java.time.Instant? = null
@org.hibernate.annotations.ColumnDefault("false")
@jakarta.persistence.Column(name = "deleted")
open var deleted: Boolean? = null

}
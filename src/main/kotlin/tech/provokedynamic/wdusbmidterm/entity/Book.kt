package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "books", schema = "public")
open class Book {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "books_id_gen")
@jakarta.persistence.SequenceGenerator(name = "books_id_gen", sequenceName = "books_id_seq", allocationSize = 1)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 255)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "title", nullable = false)
open var title: String = ""
@jakarta.validation.constraints.Size(max = 13)
@jakarta.persistence.Column(name = "isbn", length = 13)
open var isbn: String? = null
@jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
@jakarta.persistence.JoinColumn(name = "publisher_id")
open var publisher: tech.provokedynamic.wdusbmidterm.entity.Publisher? = null
@jakarta.persistence.Column(name = "publication_date")
open var publicationDate: java.time.LocalDate? = null
@jakarta.persistence.Column(name = "page_count")
open var pageCount: Int? = null
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "created_at", nullable = false)
open var createdAt: java.time.Instant = java.time.Instant.now()
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "updated_at", nullable = false)
open var updatedAt: java.time.Instant = java.time.Instant.now()
@jakarta.persistence.Column(name = "deleted_at")
open var deletedAt: java.time.Instant? = null

}
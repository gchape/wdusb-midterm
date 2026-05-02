package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "authors", schema = "public")
open class Author {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "authors_id_gen")
@jakarta.persistence.SequenceGenerator(name = "authors_id_gen", sequenceName = "authors_id_seq", allocationSize = 1)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 50)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "first_name", nullable = false, length = 50)
open var firstName: String = ""
@jakarta.validation.constraints.Size(max = 60)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "last_name", nullable = false, length = 60)
open var lastName: String = ""
@jakarta.persistence.Column(name = "bio", length = Integer.MAX_VALUE)
open var bio: String? = null
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "created_at", nullable = false)
open var createdAt: java.time.Instant = java.time.Instant.now()
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "updated_at", nullable = false)
open var updatedAt: java.time.Instant = java.time.Instant.now()

}
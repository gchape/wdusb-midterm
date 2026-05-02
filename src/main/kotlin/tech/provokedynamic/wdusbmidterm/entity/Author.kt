package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "authors")
open class Author {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 50)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "firstname", nullable = false, length = 50)
open var firstname: String = ""
@jakarta.validation.constraints.Size(max = 60)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "lastname", nullable = false, length = 60)
open var lastname: String = ""
@jakarta.validation.constraints.Size(max = 255)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "email", nullable = false)
open var email: String = ""
@jakarta.persistence.Column(name = "dob")
open var dob: java.time.LocalDate? = null
@jakarta.persistence.Column(name = "bio", length = Integer.MAX_VALUE)
open var bio: String? = null
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
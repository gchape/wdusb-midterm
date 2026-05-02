package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "users", schema = "public")
open class User {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "users_id_gen")
@jakarta.persistence.SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 50)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "username", nullable = false, length = 50)
open var username: String = ""
@jakarta.validation.constraints.Size(max = 255)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "email", nullable = false)
open var email: String = ""
@jakarta.validation.constraints.Size(max = 255)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "password_hash", nullable = false)
open var passwordHash: String = ""
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
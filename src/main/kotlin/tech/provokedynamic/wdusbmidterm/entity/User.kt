package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import java.time.Instant

@Entity
@Table(name = "users", schema = "public")
open class User(
    @NotNull
    @Size(max = 50)
    @Column(name = "username", length = 50)
    var username: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "email")
    var email: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "password_hash")
    var passwordHash: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Long = 0
        protected set

    @NotNull
    @Column(name = "created_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant = Instant.now()
        protected set

    @NotNull
    @Column(name = "updated_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var updatedAt: Instant = Instant.now()
        protected set

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
}
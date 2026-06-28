package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import org.jetbrains.annotations.NotNull
import tech.provokedynamic.wdusbmidterm.model.Role
import java.time.Instant

@Entity
@Table(name = "users", schema = "public")
open class User(
    @NotNull
    @Size(max = 100)
    @Column(name = "username", length = 100, unique = true, nullable = false)
    var username: String,

    @NotNull
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: Role = Role.USER,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @NotNull
    @Column(name = "created_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant = Instant.now()
        protected set
}
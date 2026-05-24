package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import org.jetbrains.annotations.NotNull
import tech.provokedynamic.wdusbmidterm.model.view.Role
import java.time.Instant

@Entity
@Table(name = "users", schema = "public")
open class User (

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    val id: Long = 0

    @NotNull
    @Column(name = "created_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant = Instant.now()
        protected set
}

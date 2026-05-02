package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "users")
open class User(
    @NotNull
    @Size(max = 255)
    @Column(name = "username")
    var username: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int = 0
        protected set
}
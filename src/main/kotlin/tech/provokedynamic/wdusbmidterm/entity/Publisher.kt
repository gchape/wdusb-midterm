package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SourceType
import tech.provokedynamic.wdusbmidterm.dto.response.PublisherResponse
import java.time.Instant

@Entity
@Table(name = "publishers", schema = "public")
open class Publisher(
    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "publishers_id_gen")
    @SequenceGenerator(name = "publishers_id_gen", sequenceName = "publishers_id_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Long = 0
        protected set

    @NotNull
    @Column(name = "created_at", insertable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp(source = SourceType.DB)
    var createdAt: Instant = Instant.now()
        protected set

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
}

fun Publisher.toResponse(): PublisherResponse = object : PublisherResponse {
    override val id: Long
        get() = this@toResponse.id

    override val name: String
        get() = this@toResponse.name
}
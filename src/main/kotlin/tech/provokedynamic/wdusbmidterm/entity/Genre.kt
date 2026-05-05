package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse

@Entity
@Table(name = "genres", schema = "public")
open class Genre(
    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50)
    var name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genres_id_gen")
    @SequenceGenerator(name = "genres_id_gen", sequenceName = "genres_id_seq", allocationSize = 1)
    @Column(name = "id")
    var id: Long = 0
        protected set

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    var books: MutableSet<Book> = mutableSetOf()
}

fun Genre.toResponse(): GenreResponse = object : GenreResponse {
    override val id: Long
        get() = this@toResponse.id
    override val name: String
        get() = this@toResponse.name
}
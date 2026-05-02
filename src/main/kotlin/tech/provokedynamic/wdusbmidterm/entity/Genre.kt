package tech.provokedynamic.wdusbmidterm.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

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
    var books: MutableList<Book> = mutableListOf()
}
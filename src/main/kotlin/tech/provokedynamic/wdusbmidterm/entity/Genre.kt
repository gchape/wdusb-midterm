package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "genres", schema = "public")
open class Genre {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "genres_id_gen")
@jakarta.persistence.SequenceGenerator(name = "genres_id_gen", sequenceName = "genres_id_seq", allocationSize = 1)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 50)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "name", nullable = false, length = 50)
open var name: String = ""

}
package tech.provokedynamic.wdusbmidterm.entity
        
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "publishers", schema = "public")
open class Publisher {
@jakarta.persistence.Id
@jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "publishers_id_gen")
@jakarta.persistence.SequenceGenerator(name = "publishers_id_gen", sequenceName = "publishers_id_seq", allocationSize = 1)
@jakarta.persistence.Column(name = "id", nullable = false)
open var id: Int = 0
@jakarta.validation.constraints.Size(max = 255)
@jakarta.validation.constraints.NotNull
@jakarta.persistence.Column(name = "name", nullable = false)
open var name: String = ""
@jakarta.validation.constraints.NotNull
@org.hibernate.annotations.ColumnDefault("CURRENT_TIMESTAMP")
@jakarta.persistence.Column(name = "created_at", nullable = false)
open var createdAt: java.time.Instant = java.time.Instant.now()

}
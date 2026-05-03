package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Publisher
import tech.provokedynamic.wdusbmidterm.model.projection.PublisherResponse

@Repository
interface PublisherRepository : JpaRepository<Publisher, Long> {
    fun findAllByDeletedAtNullOrderByNameAsc(): List<PublisherResponse>
    fun findByIdAndDeletedAtNull(id: Long): Publisher?
    fun existsByNameIgnoreCaseAndDeletedAtNull(name: String): Boolean
}
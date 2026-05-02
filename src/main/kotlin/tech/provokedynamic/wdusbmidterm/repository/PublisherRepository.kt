package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Publisher

@Repository
interface PublisherRepository : JpaRepository<Publisher, Long> {
    fun findAllByDeletedAtNullOrderByNameAsc(): List<Publisher>
}
package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.cache.annotation.CachePut
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.dto.response.PublisherResponse
import tech.provokedynamic.wdusbmidterm.entity.Publisher

@Repository
interface PublisherRepository : JpaRepository<Publisher, Long> {

    @CachePut("publishers:all", key = "'list'", unless = "#result.isEmpty()")
    fun findAllByDeletedAtNullOrderByNameAsc(): List<PublisherResponse>

    fun findByIdAndDeletedAtNull(id: Long): Publisher?

    @CachePut("publishers:single", key = "#id", unless = "#result == null")
    fun findPublisherById(id: Long): PublisherResponse?

    @CachePut("publishers:exists", key = "#name")
    fun existsByNameIgnoreCaseAndDeletedAtNull(name: String): Boolean
}
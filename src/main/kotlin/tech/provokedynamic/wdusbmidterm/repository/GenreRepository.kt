package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.cache.annotation.CachePut
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse
import tech.provokedynamic.wdusbmidterm.entity.Genre

@Repository
interface GenreRepository : JpaRepository<Genre, Long> {

    @CachePut("genres:all", key = "'list'", unless = "#result.isEmpty()")
    fun findAllByOrderByNameAsc(): List<GenreResponse>

    @CachePut("genres:single", key = "#id", unless = "#result == null")
    fun findGenreById(id: Long): GenreResponse?

    @CachePut("genres:exists", key = "#name")
    fun existsByNameIgnoreCase(name: String): Boolean
}
package tech.provokedynamic.wdusbmidterm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.provokedynamic.wdusbmidterm.entity.Genre
import tech.provokedynamic.wdusbmidterm.model.projection.GenreResponse

@Repository
interface GenreRepository : JpaRepository<Genre, Long> {
    fun findAllByOrderByNameAsc(): List<GenreResponse>
}
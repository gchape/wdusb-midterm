package tech.provokedynamic.wdusbmidterm.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.dto.request.GenreRequest
import tech.provokedynamic.wdusbmidterm.dto.response.GenreResponse
import tech.provokedynamic.wdusbmidterm.entity.Genre
import tech.provokedynamic.wdusbmidterm.entity.toResponse
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository

@Service
class GenreService(private val genreRepository: GenreRepository) {

    @Cacheable("genres:all", key = "'list'", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    fun getAllGenres(): List<GenreResponse> =
        genreRepository.findAllByOrderByNameAsc()

    @Cacheable("genres:single", key = "#id")
    @Transactional(readOnly = true)
    fun getGenreById(id: Long): GenreResponse =
        genreRepository.findGenreById(id)
            ?: throw EntityNotFoundException("Genre $id not found")

    @Caching(
        evict = [
            CacheEvict("genres:all", allEntries = true)
        ]
    )
    @Transactional
    fun createGenre(request: GenreRequest): GenreResponse {
        if (genreRepository.existsByNameIgnoreCase(request.name.trim()))
            throw EntityAlreadyExistsException("Genre '${request.name}' already exists")

        return genreRepository.save(Genre(name = request.name.trim())).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict("genres:all", allEntries = true),
            CacheEvict("genres:single", key = "#id")
        ]
    )
    @Transactional
    fun updateGenre(id: Long, request: GenreRequest): GenreResponse {
        val genre = genreRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Genre $id not found") }

        genre.name = request.name.trim()

        return genreRepository.save(genre).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict("genres:all", allEntries = true),
            CacheEvict("genres:single", key = "#id")
        ]
    )
    @Transactional
    fun deleteGenre(id: Long) {
        if (!genreRepository.existsById(id))
            throw EntityNotFoundException("Genre $id not found")
        genreRepository.deleteById(id)
    }
}
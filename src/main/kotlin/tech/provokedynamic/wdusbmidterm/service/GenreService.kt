package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.entity.Genre
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.model.dto.GenreRequest
import tech.provokedynamic.wdusbmidterm.model.projection.GenreResponse
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository

@Service
class GenreService(private val genreRepository: GenreRepository) {

    @Transactional(readOnly = true)
    fun getAllGenres(): List<GenreResponse> =
        genreRepository.findAllByOrderByNameAsc()

    @Transactional(readOnly = true)
    fun getGenreById(id: Long): GenreResponse =
        genreRepository.findGenreById(id)
            ?: throw EntityNotFoundException("Genre $id not found")

    @Transactional
    fun createGenre(request: GenreRequest): GenreResponse {
        if (genreRepository.existsByNameIgnoreCase(request.name.trim()))
            throw EntityAlreadyExistsException("Genre '${request.name}' already exists")
        val saved = genreRepository.save(Genre(name = request.name.trim()))
        return genreRepository.findGenreById(saved.id)!!
    }

    @Transactional
    fun updateGenre(id: Long, request: GenreRequest): GenreResponse {
        val genre = genreRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Genre $id not found") }
        genre.name = request.name.trim()
        genreRepository.save(genre)
        return genreRepository.findGenreById(id)!!
    }

    @Transactional
    fun deleteGenre(id: Long) {
        if (!genreRepository.existsById(id))
            throw EntityNotFoundException("Genre $id not found")
        genreRepository.deleteById(id)
    }
}
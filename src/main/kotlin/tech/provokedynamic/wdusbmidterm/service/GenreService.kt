package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.model.dto.GenreResponseDTO
import tech.provokedynamic.wdusbmidterm.model.dto.toResponseDto
import tech.provokedynamic.wdusbmidterm.repository.GenreRepository

@Service
class GenreService(private val genreRepository: GenreRepository) {

    @Transactional(readOnly = true)
    fun getAllGenres(): List<GenreResponseDTO> =
        genreRepository.findAllByOrderByNameAsc().map { it.toResponseDto() }
}

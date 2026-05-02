package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.model.dto.PublisherResponseDTO
import tech.provokedynamic.wdusbmidterm.model.dto.toResponseDto
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository

@Service
class PublisherService(private val publisherRepository: PublisherRepository) {

    @Transactional(readOnly = true)
    fun getAllPublishers(): List<PublisherResponseDTO> =
        publisherRepository.findAllByDeletedAtNullOrderByNameAsc().map { it.toResponseDto() }
}
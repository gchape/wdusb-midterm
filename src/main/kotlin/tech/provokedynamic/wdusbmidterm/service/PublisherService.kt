package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.entity.Publisher
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.model.dto.PublisherRequest
import tech.provokedynamic.wdusbmidterm.model.projection.PublisherResponse
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository
import java.time.Instant

@Service
class PublisherService(private val publisherRepository: PublisherRepository) {

    @Transactional(readOnly = true)
    fun getAllPublishers(): List<PublisherResponse> =
        publisherRepository.findAllByDeletedAtNullOrderByNameAsc()

    @Transactional(readOnly = true)
    fun getPublisherById(id: Long): PublisherResponse =
        publisherRepository.findPublisherById(id)
            ?: throw EntityNotFoundException("Publisher $id not found")

    @Transactional
    fun createPublisher(request: PublisherRequest): PublisherResponse {
        if (publisherRepository.existsByNameIgnoreCaseAndDeletedAtNull(request.name.trim()))
            throw EntityAlreadyExistsException("Publisher '${request.name}' already exists")

        val saved = publisherRepository.save(Publisher(name = request.name.trim()))

        return publisherRepository.findPublisherById(saved.id)!!
    }

    @Transactional
    fun updatePublisher(id: Long, request: PublisherRequest): PublisherResponse {
        val publisher = publisherRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Publisher $id not found")
        publisher.name = request.name.trim()
        publisherRepository.save(publisher)
        return publisherRepository.findPublisherById(id)!!
    }

    @Transactional
    fun softDeletePublisher(id: Long) {
        val publisher = publisherRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Publisher $id not found")
        publisher.deletedAt = Instant.now()
        publisherRepository.save(publisher)
    }
}
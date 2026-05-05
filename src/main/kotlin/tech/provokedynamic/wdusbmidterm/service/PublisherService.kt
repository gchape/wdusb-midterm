package tech.provokedynamic.wdusbmidterm.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.dto.request.PublisherRequest
import tech.provokedynamic.wdusbmidterm.dto.response.PublisherResponse
import tech.provokedynamic.wdusbmidterm.entity.Publisher
import tech.provokedynamic.wdusbmidterm.entity.toResponse
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.exception.EntityDeletedException
import tech.provokedynamic.wdusbmidterm.exception.EntityNotFoundException
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository
import java.time.Instant

@Service
class PublisherService(private val publisherRepository: PublisherRepository) {

    @Cacheable("publishers:all", key = "'list'", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    fun getAllPublishers(): List<PublisherResponse> =
        publisherRepository.findAllByDeletedAtNullOrderByNameAsc()

    @Cacheable("publishers:single", key = "#id")
    @Transactional(readOnly = true)
    fun getPublisherById(id: Long): PublisherResponse =
        publisherRepository.findPublisherById(id)
            ?: throw EntityNotFoundException("Publisher $id not found")

    @Caching(
        evict = [
            CacheEvict("publishers:all", allEntries = true)
        ]
    )
    @Transactional
    fun createPublisher(request: PublisherRequest): PublisherResponse {
        if (publisherRepository.existsByNameIgnoreCaseAndDeletedAtNull(request.name.trim()))
            throw EntityAlreadyExistsException("Publisher '${request.name}' already exists")

        return publisherRepository.save(Publisher(name = request.name.trim())).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict("publishers:all", allEntries = true),
            CacheEvict("publishers:single", key = "#id")
        ]
    )
    @Transactional
    fun updatePublisher(id: Long, request: PublisherRequest): PublisherResponse {
        val publisher = publisherRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Publisher $id not found")

        publisher.name = request.name.trim()

        return publisherRepository.save(publisher).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict("publishers:all", allEntries = true),
            CacheEvict("publishers:single", key = "#id")
        ]
    )
    @Transactional
    fun softDeletePublisher(id: Long) {
        val publisher = publisherRepository.findByIdAndDeletedAtNull(id)
            ?: throw EntityNotFoundException("Publisher $id not found")

        publisher.deletedAt?.let { throw EntityDeletedException("Publisher $id is already deleted") }

        publisher.deletedAt = Instant.now()

        publisherRepository.save(publisher)
    }
}
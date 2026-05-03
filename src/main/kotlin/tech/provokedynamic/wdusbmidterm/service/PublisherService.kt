package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.model.projection.PublisherResponse
import tech.provokedynamic.wdusbmidterm.repository.PublisherRepository

@Service
class PublisherService(private val publisherRepository: PublisherRepository) {

    @Transactional(readOnly = true)
    fun getAllPublishers(): List<PublisherResponse> =
        publisherRepository.findAllByDeletedAtNullOrderByNameAsc()
}
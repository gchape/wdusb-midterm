package tech.provokedynamic.wdusbmidterm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tech.provokedynamic.wdusbmidterm.model.projection.BookCardProjection
import tech.provokedynamic.wdusbmidterm.repository.AuthorRepository
import tech.provokedynamic.wdusbmidterm.repository.BookRepository

@Service
class HomeService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {
    @Transactional(readOnly = true)
    fun getRecentBooks(): List<BookCardProjection> =
        bookRepository.findTop6ByDeletedAtNullOrderByIdDesc()

    @Transactional(readOnly = true)
    fun getTotalBooks(): Long = bookRepository.countByDeletedAtNull()

    @Transactional(readOnly = true)
    fun getTotalAuthors(): Long = authorRepository.countByDeletedAtNull()
}
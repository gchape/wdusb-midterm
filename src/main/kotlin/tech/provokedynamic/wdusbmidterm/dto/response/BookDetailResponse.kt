package tech.provokedynamic.wdusbmidterm.dto.response

import java.time.LocalDate

interface BookDetailResponse {
    val id: Long
    val isbn: String
    val title: String
    val pageCount: Short
    val publicationDate: LocalDate
    val publisher: PublisherResponse?
    val authors: Set<AuthorResponse>
    val genres: Set<GenreResponse>
}
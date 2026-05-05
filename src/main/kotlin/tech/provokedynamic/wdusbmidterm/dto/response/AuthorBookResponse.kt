package tech.provokedynamic.wdusbmidterm.dto.response

import java.time.LocalDate

interface AuthorBookResponse {
    val id: Long
    val title: String
    val publicationDate: LocalDate
    val genres: Set<GenreResponse>
}
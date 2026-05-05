package tech.provokedynamic.wdusbmidterm.dto.response

import java.time.LocalDate

interface BookCatalogResponse {
    val id: Long
    val title: String
    val isbn: String
    val pageCount: Short
    val publicationDate: LocalDate
    val genres: Set<GenreResponse>
}
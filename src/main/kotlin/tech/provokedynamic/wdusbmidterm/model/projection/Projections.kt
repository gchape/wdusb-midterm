package tech.provokedynamic.wdusbmidterm.model.projection

import java.time.LocalDate

interface PublisherResponse {
    val id: Long
    val name: String
}

interface AuthorResponse {
    val id: Long
    val firstName: String
    val lastName: String
    val bio: String?
}

interface GenreResponse {
    val id: Long
    val name: String
}

interface BookCatalogItem {
    val id: Long
    val title: String
    val isbn: String
    val pageCount: Short
    val publicationDate: LocalDate
    val genres: Set<GenreResponse>
}

interface BookCardProjection {
    val id: Long
    val title: String
    val publicationDate: LocalDate
}

interface BookDetailProjection {
    val id: Long
    val isbn: String
    val title: String
    val pageCount: Short
    val publicationDate: LocalDate
    val publisher: PublisherResponse?
    val authors: Set<AuthorResponse>
    val genres: Set<GenreResponse>
}

interface AuthorBookItem {
    val id: Long
    val title: String
    val publicationDate: LocalDate
    val genres: Set<GenreResponse>
}
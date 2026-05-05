package tech.provokedynamic.wdusbmidterm.dto.response

import java.time.LocalDate

interface BookCardResponse {
    val id: Long
    val title: String
    val publicationDate: LocalDate
}
package tech.provokedynamic.wdusbmidterm.dto.request

import jakarta.validation.constraints.*
import java.time.LocalDate

data class BookRequest(
    @field:NotBlank(message = "{book.title.notBlank}")
    @field:Size(max = 255, message = "{book.title.size.max}")
    val title: String,

    @field:NotBlank(message = "{book.isbn.notBlank}")
    @field:Size(min = 10, max = 13, message = "{book.isbn.size}")
    val isbn: String,

    @field:NotNull(message = "{book.publisherId.notNull}")
    @field:Positive(message = "{book.publisherId.positive}")
    var publisherId: Long? = null,

    @field:NotNull(message = "{book.publicationDate.notNull}")
    @field:PastOrPresent(message = "{book.publicationDate.pastOrPresent}")
    var publicationDate: LocalDate? = null,

    @field:NotNull(message = "{book.pageCount.notNull}")
    @field:Min(value = 1, message = "{book.pageCount.min}")
    var pageCount: Short? = null,

    @field:NotEmpty(message = "{book.genreIds.notEmpty}")
    val genreIds: List<Long> = emptyList(),

    @field:NotEmpty(message = "{book.authorIds.notEmpty}")
    val authorIds: List<Long> = emptyList()
)

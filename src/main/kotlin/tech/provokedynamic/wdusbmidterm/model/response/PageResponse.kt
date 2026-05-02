package tech.provokedynamic.wdusbmidterm.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
) {
    fun hasNext() = number + 1 < totalPages
    fun hasPrevious() = number > 0
}
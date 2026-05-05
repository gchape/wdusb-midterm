package tech.provokedynamic.wdusbmidterm.dto.response

interface AuthorResponse {
    val id: Long
    val firstName: String
    val lastName: String
    val bio: String?
}
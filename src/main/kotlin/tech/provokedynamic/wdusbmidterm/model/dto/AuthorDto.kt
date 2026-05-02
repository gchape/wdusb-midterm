package tech.provokedynamic.wdusbmidterm.model

import tech.provokedynamic.wdusbmidterm.entity.Author
import java.time.LocalDate

data class AuthorResponseDto(
    val firstname: String,
    val lastname: String,
    val email: String,
    val dob: LocalDate,
)

fun Author.toAuthorResponseDto() = AuthorResponseDto(
    firstname = firstname,
    lastname = lastname,
    email = email,
    dob = dob,
)
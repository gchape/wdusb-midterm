package tech.provokedynamic.wdusbmidterm.model.dto

import tech.provokedynamic.wdusbmidterm.entity.Author
import java.time.LocalDate

data class AuthorResponseDto(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val dob: LocalDate,
)

fun Author.toAuthorResponseDto() = AuthorResponseDto(
    id = id,
    firstname = firstname,
    lastname = lastname,
    email = email,
    dob = dob,
)
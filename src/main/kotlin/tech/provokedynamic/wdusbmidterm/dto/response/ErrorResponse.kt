package tech.provokedynamic.wdusbmidterm.dto.response

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val fields: Map<String, String>? = null   // populated for validation errors only
)

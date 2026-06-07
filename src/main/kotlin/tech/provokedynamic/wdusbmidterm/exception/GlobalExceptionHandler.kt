package tech.provokedynamic.wdusbmidterm.exception

import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException
import tech.provokedynamic.wdusbmidterm.dto.response.ErrorResponse
import java.util.Locale

@ControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource
) {

    private fun msg(key: String, locale: Locale, vararg args: Any): String =
        messageSource.getMessage(key, args.ifEmpty { null }, key, locale)!!

    private fun respond(
        status: HttpStatus,
        error: String,
        message: String,
        model: Model?,
        fields: Map<String, String>? = null
    ): Any = if (model != null) {
        model.addAttribute("status", status.value())
        model.addAttribute("message", message)
        "error"
    } else {
        ResponseEntity.status(status)
            .body(ErrorResponse(status.value(), error, message, fields))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(locale: Locale, model: Model?) =
        respond(HttpStatus.NOT_FOUND, "Not Found", msg("error.entity.notFound", locale), model)

    @ExceptionHandler(EntityDeletedException::class)
    fun handleDeleted(locale: Locale, model: Model?) =
        respond(HttpStatus.GONE, "Gone", msg("error.entity.deleted", locale), model)

    @ExceptionHandler(EntityAlreadyExistsException::class)
    fun handleConflict(locale: Locale, model: Model?) =
        respond(HttpStatus.CONFLICT, "Conflict", msg("error.entity.alreadyExists", locale), model)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, locale: Locale): ResponseEntity<ErrorResponse> {
        val fields = ex.bindingResult.fieldErrors.associate { fe ->
            fe.field to (fe.defaultMessage ?: msg("error.validation", locale))
        }
        @Suppress("UNCHECKED_CAST")
        return respond(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Unprocessable Entity",
            msg("error.validation", locale),
            null,
            fields
        ) as ResponseEntity<ErrorResponse>
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(locale: Locale, model: Model?) =
        respond(HttpStatus.NOT_FOUND, "Not Found", msg("error.notFound.page", locale), model)

    @ExceptionHandler(Exception::class)
    fun handleGeneral(locale: Locale, model: Model?) =
        respond(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", msg("error.internal", locale), model)
}

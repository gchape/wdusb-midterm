package tech.provokedynamic.wdusbmidterm.exception

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import tech.provokedynamic.wdusbmidterm.dto.response.ErrorResponse
import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource
) {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    private fun msg(key: String, locale: Locale): String =
        messageSource.getMessage(key, null, key, locale)!!

    private fun error(status: HttpStatus, error: String, message: String, fields: Map<String, String>? = null) =
        ResponseEntity.status(status).body(ErrorResponse(status.value(), error, message, fields))

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException, locale: Locale): ResponseEntity<ErrorResponse> {
        log.debug("Entity not found: {}", ex.message)
        return error(HttpStatus.NOT_FOUND, "Not Found", msg("error.entity.notFound", locale))
    }

    @ExceptionHandler(EntityDeletedException::class)
    fun handleDeleted(ex: EntityDeletedException, locale: Locale): ResponseEntity<ErrorResponse> {
        log.debug("Entity deleted: {}", ex.message)
        return error(HttpStatus.GONE, "Gone", msg("error.entity.deleted", locale))
    }

    @ExceptionHandler(EntityAlreadyExistsException::class)
    fun handleConflict(ex: EntityAlreadyExistsException, locale: Locale): ResponseEntity<ErrorResponse> {
        log.debug("Entity conflict: {}", ex.message)
        return error(HttpStatus.CONFLICT, "Conflict", msg("error.entity.alreadyExists", locale))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, locale: Locale): ResponseEntity<ErrorResponse> {
        val fields = ex.bindingResult.fieldErrors.associate { fe ->
            fe.field to (fe.defaultMessage ?: msg("error.validation", locale))
        }
        log.debug("Validation failed: {}", fields)
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", msg("error.validation", locale), fields)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException, locale: Locale): ResponseEntity<ErrorResponse> {
        log.debug("No resource found: {}", ex.message)
        return error(HttpStatus.NOT_FOUND, "Not Found", msg("error.notFound.page", locale))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception, locale: Locale): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception: {}", ex.message, ex)
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", msg("error.internal", locale))
    }
}

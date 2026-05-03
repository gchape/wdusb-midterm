package tech.provokedynamic.wdusbmidterm.exception

package tech.provokedynamic.wdusbmidterm.exception

import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: EntityNotFoundException, model: Model): String {
        model.addAttribute("status", 404)
        model.addAttribute("message", ex.message)
        return "error"
    }

    @ExceptionHandler(EntityDeletedException::class)
    @ResponseStatus(HttpStatus.GONE)
    fun handleDeleted(ex: EntityDeletedException, model: Model): String {
        model.addAttribute("status", 410)
        model.addAttribute("message", ex.message)
        return "error"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(ex: Exception, model: Model): String {
        model.addAttribute("status", 500)
        model.addAttribute("message", "Something went wrong. Please try again.")
        return "error"
    }
}
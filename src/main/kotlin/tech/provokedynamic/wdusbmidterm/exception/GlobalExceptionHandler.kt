package tech.provokedynamic.wdusbmidterm.exception

import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.resource.NoResourceFoundException

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

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflict(ex: EntityAlreadyExistsException, model: Model): String {
        model.addAttribute("status", 409)
        model.addAttribute("message", ex.message)
        return "error"
    }

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoResourceFound(model: Model): String {
        model.addAttribute("status", 404)
        model.addAttribute("message", "Page not found.")
        return "error"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(model: Model): String {
        model.addAttribute("status", 500)
        model.addAttribute("message", "Something went wrong. Please try again.")
        return "error"
    }
}
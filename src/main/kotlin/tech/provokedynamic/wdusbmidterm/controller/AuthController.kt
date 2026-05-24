package tech.provokedynamic.wdusbmidterm.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.support.RequestContextUtils
import tech.provokedynamic.wdusbmidterm.dto.request.RegisterRequest
import tech.provokedynamic.wdusbmidterm.exception.EntityAlreadyExistsException
import tech.provokedynamic.wdusbmidterm.model.view.toErrorMap
import tech.provokedynamic.wdusbmidterm.service.UserService

@Controller
class AuthController(private val userService: UserService) {

    @GetMapping("/login")
    fun loginPage(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?,
        request: HttpServletRequest,
        csrf: CsrfToken,
        model: Model
    ): String {
        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password.")
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.")
        }

        val flashMap = RequestContextUtils.getInputFlashMap(request)
        if (flashMap != null && flashMap.containsKey("flashSuccess")) {
            model.addAttribute("flashSuccess", flashMap["flashSuccess"])
        }

        model.addAttribute("request", request)
        model.addAttribute("csrf", csrf)
        return "auth/login"
    }

    @GetMapping("/register")
    fun registerPage(request: HttpServletRequest, model: Model): String {
        model.addAttribute("request", request)
        model.addAttribute("requestData", RegisterRequest("", "", ""))
        return "auth/register"
    }

    @PostMapping("/register")
    fun register(
        @Valid @ModelAttribute("requestData") registerRequest: RegisterRequest,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        model.addAttribute("request", request)

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.toErrorMap())
            return "auth/register"
        }

        if (!registerRequest.passwordsMatch) {
            model.addAttribute("errors", mapOf("confirmPassword" to "Passwords do not match"))
            return "auth/register"
        }

        return try {
            userService.register(registerRequest)
            redirectAttributes.addFlashAttribute("flashSuccess", "Account created! You can now log in.")
            "redirect:/login"
        } catch (e: EntityAlreadyExistsException) {
            model.addAttribute("errors", mapOf("username" to e.message))
            "auth/register"
        }
    }
}
package tech.provokedynamic.wdusbmidterm.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import tech.provokedynamic.wdusbmidterm.service.UserService

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(private val userService: UserService) {

    @GetMapping
    fun dashboard(request: HttpServletRequest, model: Model): String {
        model.addAttribute("request", request)
        model.addAttribute("users", userService.getAllUsers())
        return "admin/dashboard"
    }

    @PostMapping("/users/{id}/disable")
    fun disableUser(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        userService.setUserEnabled(id, false)
        redirectAttributes.addFlashAttribute("flashSuccess", "User #$id disabled.")
        return "redirect:/admin"
    }

    @PostMapping("/users/{id}/enable")
    fun enableUser(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        userService.setUserEnabled(id, true)
        redirectAttributes.addFlashAttribute("flashSuccess", "User #$id enabled.")
        return "redirect:/admin"
    }

    @PostMapping("/users/{id}/promote")
    fun promoteUser(
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        userService.promoteToAdmin(id)
        redirectAttributes.addFlashAttribute("flashSuccess", "User #$id promoted to ADMIN.")
        return "redirect:/admin"
    }
}
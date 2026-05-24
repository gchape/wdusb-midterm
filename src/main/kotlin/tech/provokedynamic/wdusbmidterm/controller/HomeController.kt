package tech.provokedynamic.wdusbmidterm.controller

import jakarta.servlet.http.HttpServletRequest // Add this import
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import tech.provokedynamic.wdusbmidterm.service.HomeService

@Controller
class HomeController(
    private val homeService: HomeService
) {
    @GetMapping("/")
    fun home(request: HttpServletRequest, model: Model): String {
        model.addAttribute("request", request)
        model.addAttribute("recentBooks", homeService.getRecentBooks())
        model.addAttribute("totalBooks", homeService.getTotalBooks())
        model.addAttribute("totalAuthors", homeService.getTotalAuthors())

        return "index"
    }
}
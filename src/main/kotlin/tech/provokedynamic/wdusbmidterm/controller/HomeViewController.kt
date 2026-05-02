package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import tech.provokedynamic.wdusbmidterm.service.BookService

@Controller
@RequestMapping("/")
class HomeViewController(val bookService: BookService) {

    @GetMapping(produces = ["text/html;charset=utf-8"])
    fun home(
        model: Model,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int,
    ): String {
        val pageable: Pageable = PageRequest.of(page, pageSize)
        model.addAttribute("recentBooks", bookService.getRecentBooks(pageable))
        return "home"
    }

}
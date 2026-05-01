package tech.provokedynamic.wdusbmidterm.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping(produces = ["text/html;charset=utf-8"])
    fun home(): String {
        return "home"
    }

}
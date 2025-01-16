package com.wutsi.koki.portal.email.page

import com.wutsi.koki.portal.email.service.EmailService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class EmailController(private val service: EmailService) {
    @GetMapping("/emails/{id}")
    fun show(@PathVariable id: String, model: Model): String{
        val email = service.email(id)
        model.addAttribute("email", email)
        return "emails/show"
    }
}

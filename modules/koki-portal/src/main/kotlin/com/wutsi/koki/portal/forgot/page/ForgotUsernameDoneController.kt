package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.forgot.form.ForgotForm
import com.wutsi.koki.portal.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/forgot/password/done")
class ForgotPasswordDoneController(private val service: UserService) : AbstractPageController() {
    @GetMapping
    fun index(id: Long, model: Model): String {
        val user = service.user(id, fullGraph = false)
        model.addAttribute("email", user.email)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_USERNAME_DONE,
                title = getMessage("page.forgot.password.meta.title"),
            )
        )
        return "forgot/password-done"
    }
}

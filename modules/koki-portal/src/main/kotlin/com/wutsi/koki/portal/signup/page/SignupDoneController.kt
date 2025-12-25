package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/signup/done")
class SignupDoneController : AbstractSignupController() {
    @GetMapping
    fun index(id: Long, model: Model): String {
        val user = resolveUser(id)
        model.addAttribute("me", user)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP_DONE,
                title = getMessage("page.signup.meta.title"),
            )
        )
        return "signup/done"
    }
}

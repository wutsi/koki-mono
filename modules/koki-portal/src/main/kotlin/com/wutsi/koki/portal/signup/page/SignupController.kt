package com.wutsi.koki.portal.signup.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.signup.form.SignupForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/signup")
class SignupController : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("form", SignupForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.SIGNUP,
                title = getMessage("page.signup.meta.title"),
            )
        )
        return "signup/index"
    }

    @PostMapping
    fun submit(@ModelAttribute form: SignupForm, model: Model): String {
        return "redirect:/signup/profile"
    }
}

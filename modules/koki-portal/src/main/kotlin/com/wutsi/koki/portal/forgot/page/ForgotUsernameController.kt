package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.forgot.form.EmailForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/forgot/username")
class ForgotUsernameController : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("form", EmailForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_USERNAME,
                title = getMessage("page.forgot.username.meta.title"),
            )
        )
        return "forgot/username"
    }

    @PostMapping
    fun submit(@ModelAttribute form: EmailForm, model: Model): String {
        model.addAttribute("email", form.email)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_USERNAME,
                title = getMessage("page.forgot.username.meta.title"),
            )
        )
        return "forgot/username-done"
    }
}

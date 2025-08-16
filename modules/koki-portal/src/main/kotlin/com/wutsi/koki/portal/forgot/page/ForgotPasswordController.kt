package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.forgot.form.ForgotForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/forgot/password")
class ForgotPasswordController : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("form", ForgotForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_USERNAME,
                title = getMessage("page.forgot.password.meta.title"),
            )
        )
        return "forgot/password"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ForgotForm, model: Model): String {
        model.addAttribute("email", form.email)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_USERNAME,
                title = getMessage("page.forgot.password.meta.title"),
            )
        )
        return "redirect/forgot/password/done?id=$"
    }
}

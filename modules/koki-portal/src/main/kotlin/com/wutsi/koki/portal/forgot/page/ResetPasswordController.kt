package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.forgot.form.ResetPasswordForm
import com.wutsi.koki.portal.forgot.service.ForgotService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestClientException

@Controller
@RequestMapping("/forgot/password/reset")
class ResetPasswordController(private val service: ForgotService) : AbstractPageController() {
    @GetMapping
    fun index(@RequestParam token: String, model: Model): String {
        return index(ResetPasswordForm(tokenId = token), model)
    }

    private fun index(form: ResetPasswordForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_PASSWORD_RESET,
                title = getMessage("page.forgot.password.reset.meta.title"),
            )
        )
        return "forgot/password-reset"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ResetPasswordForm, model: Model): String {
        try {
            service.resetPassword(form)
            return "redirect:/forgot/password/reset/done"
        } catch (ex: RestClientException) {
            loadError(ex, model)
            return index(form, model)
        }
    }
}

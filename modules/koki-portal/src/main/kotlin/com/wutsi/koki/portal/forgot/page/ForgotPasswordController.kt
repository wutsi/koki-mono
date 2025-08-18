package com.wutsi.koki.portal.forgot.page

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.forgot.form.ForgotForm
import com.wutsi.koki.portal.forgot.service.ForgotService
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/forgot/password")
class ForgotPasswordController(private val service: ForgotService) : AbstractPageController() {
    @GetMapping
    fun index(model: Model): String {
        return index(ForgotForm(), model)
    }

    private fun index(form: ForgotForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.FORGOT_PASSWORD,
                title = getMessage("page.forgot.password.meta.title"),
            )
        )
        return "forgot/password"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ForgotForm, model: Model): String {
        try {
            service.sendPassword(form)
            val encodedEmail = Base64().encodeAsString(form.email.toByteArray())
            return "redirect:/forgot/password/done?e=$encodedEmail"
        } catch (ex: HttpClientErrorException) {
            loadError(ex, model)
            return index(form, model)
        }
    }

    override fun toErrorMessage(response: ErrorResponse): String {
        return when (response.error.code) {
            ErrorCode.USER_NOT_FOUND -> getMessage("error.user.email-not-found")
            else -> super.toErrorMessage(response)
        }
    }
}

package com.wutsi.koki.portal.page.settings.smtp

import com.wutsi.koki.portal.email.model.SMTPForm
import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SettingsSMTPController(
    private val service: ConfigurationService,
    private val httpRequest: HttpServletRequest,
) : AbstractPageController() {
    @GetMapping("/settings/email/smtp")
    fun show(
        @RequestParam(required = false) updated: Long? = null,
        model: Model,
    ): String {
        val config = service.configurations(keyword = "smtp.")
        if (config.isNotEmpty()) {
            val form = SMTPForm(
                host = config[ConfigurationName.SMTP_HOST] ?: "",
                username = config[ConfigurationName.SMTP_USERNAME] ?: "",
                password = config[ConfigurationName.SMTP_PASSWORD] ?: "",
                fromAddress = config[ConfigurationName.SMTP_FROM_ADDRESS] ?: "",
                fromPersonal = config[ConfigurationName.SMTP_FROM_PERSONAL] ?: "",
            )
            model.addAttribute("form", form)
        }
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.EMAIL_SETTINGS_SMTP,
                title = "SMTP Settings"
            )
        )
        loadUpdatedToast(updated, model)
        return "emails/settings/smtp/show"
    }

    private fun loadUpdatedToast(updated: Long?, model: Model) {
        val referer = httpRequest.getHeader(HttpHeaders.REFERER)
        if (
            updated != null &&
            referer != null &&
            referer.endsWith("/settings/email/smtp/edit")
        ) {
            model.addAttribute("toast", "Saved")
        }
    }
}

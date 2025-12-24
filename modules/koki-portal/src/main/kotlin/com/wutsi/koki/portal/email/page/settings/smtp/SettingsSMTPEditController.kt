package com.wutsi.koki.portal.page.settings.smtp

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.email.model.SMTPForm
import com.wutsi.koki.portal.email.service.SMTPValidator
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestClientException
import java.io.IOException

@Controller
@RequestMapping("/settings/email/smtp")
@RequiresPermission(["email:admin"])
class SettingsSMTPEditController(
    private val service: ConfigurationService,
    private val validator: SMTPValidator,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsSMTPEditController::class.java)
    }

    @GetMapping("/edit")
    fun edit(model: Model): String {
        val config = service.configurations(keyword = "smtp.")
        val form = SMTPForm(
            type = config[ConfigurationName.SMTP_TYPE] ?: "KOKI",
            host = config[ConfigurationName.SMTP_HOST] ?: "",
            username = config[ConfigurationName.SMTP_USERNAME] ?: "",
            password = config[ConfigurationName.SMTP_PASSWORD] ?: "",
            fromAddress = config[ConfigurationName.SMTP_FROM_ADDRESS] ?: "",
            fromPersonal = config[ConfigurationName.SMTP_FROM_PERSONAL] ?: "",
        )
        return edit(form, model)
    }

    private fun edit(form: SMTPForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMAIL_SETTINGS_SMTP_EDIT,
                title = "Mail Server"
            )
        )
        model.addAttribute("types", listOf("KOKI", "EXTERNAL"))
        return "emails/settings/smtp/edit"
    }

    @PostMapping("/save")
    fun save(@ModelAttribute form: SMTPForm, model: Model): String {
        try {
            if (form.type == "EXTERNAL") {
                validator.validate(form.host, form.port, form.username)
            }
            service.save(form)
            return "redirect:/settings/email/smtp?_toast=1&_ts=" + System.currentTimeMillis()
        } catch (ex: IOException) {
            LOGGER.error("Bad SMTP configuration", ex)
            model.addAttribute("error", ErrorCode.EMAIL_INVALID_SMTP_CONFIGURATION)
            return edit(form, model)
        } catch (ex: RestClientException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}

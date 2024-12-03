package com.wutsi.koki.portal.page.settings.smtp

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.SMTPValidator
import com.wutsi.koki.portal.service.TenantService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import java.io.IOException

@Controller
class SMTPController(
    private val service: TenantService,
    private val validator: SMTPValidator,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMTPController::class.java)
    }

    @GetMapping("/settings/smtp")
    fun show(model: Model): String {
        val config = service.configurations(keyword = "smtp")
        val form = SMTPForm(
            host = config[ConfigurationName.SMTP_HOST] ?: "",
            username = config[ConfigurationName.SMTP_USERNAME] ?: "",
            password = config[ConfigurationName.SMTP_PASSWORD] ?: "",
            fromAddress = config[ConfigurationName.SMTP_FROM_ADDRESS] ?: "",
            fromPersonal = config[ConfigurationName.SMTP_FROM_PERSONAL] ?: "",
        )
        return show(form, model)
    }

    private fun show(form: SMTPForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.SETTINGS_SMTP,
                title = "Mail Server"
            )
        )
        return "settings/smtp/show"
    }

    @PostMapping("/settings/smtp/save")
    fun save(@ModelAttribute form: SMTPForm, model: Model): String {
        try {
            validator.validate(form.host, form.port, form.username)
            service.save(form)
            model.addAttribute(
                "page",
                PageModel(
                    name = PageName.SETTINGS_SMTP_SAVED,
                    title = "Mail Server"
                )
            )
            return "settings/smtp/saved"
        } catch (ex: IOException) {
            LOGGER.error("Connection to SMTP server failed", ex)
            model.addAttribute("error", "The settings are not valid. Unable to connect to the Mail Server")
            return show(form, model)
        }
    }
}

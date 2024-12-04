package com.wutsi.koki.portal.page.settings.smtp

import com.wutsi.koki.portal.model.PageModel
import com.wutsi.koki.portal.page.AbstractPageController
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.portal.service.TenantService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ShowSMTPController(
    private val service: TenantService,
) : AbstractPageController() {
    @GetMapping("/settings/smtp")
    fun show(model: Model): String {
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
                name = PageName.SETTINGS_SMTP,
                title = "Mail Server"
            )
        )
        return "settings/smtp/show"
    }
}

package com.wutsi.koki.portal.page.settings.smtp

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.email.model.SMTPForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["email:admin"])
class SettingsSMTPController(
    private val service: ConfigurationService,
) : AbstractPageController() {
    @GetMapping("/settings/email/smtp")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        model: Model,
    ): String {
        val config = service.configurations(keyword = "smtp.")
        val form = SMTPForm(
            type = config[ConfigurationName.SMTP_TYPE] ?: "KOKI",
            host = config[ConfigurationName.SMTP_HOST] ?: "",
            username = config[ConfigurationName.SMTP_USERNAME] ?: "",
            password = config[ConfigurationName.SMTP_PASSWORD] ?: "",
            fromAddress = config[ConfigurationName.SMTP_FROM_ADDRESS] ?: "",
            fromPersonal = config[ConfigurationName.SMTP_FROM_PERSONAL] ?: "",
        )
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.EMAIL_SETTINGS_SMTP,
                title = "SMTP Settings"
            )
        )
        loadToast(referer, toast, timestamp, model)
        return "emails/settings/smtp/show"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        model: Model
    ) {
        if (toast != null && canShowToasts(timestamp, referer, listOf("/settings/email/smtp/edit"))) {
            model.addAttribute("toast", "Saved")
        }
    }
}

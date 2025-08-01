package com.wutsi.koki.portal.invoice.page.settings

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationForm
import com.wutsi.koki.portal.invoice.form.InvoiceSettingsForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller()
@RequiresPermission(["invoice:admin"])
class SettingsInvoiceController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/invoices")
    fun show(
        model: Model
    ): String {
        val configs = service.configurations(
            names = listOf(
                ConfigurationName.INVOICE_DUE_DAYS,
                ConfigurationName.INVOICE_START_NUMBER,
                ConfigurationName.INVOICE_EMAIL_ENABLED,
                ConfigurationName.INVOICE_EMAIL_SUBJECT,
            )
        )
        model.addAttribute(
            "form",
            InvoiceSettingsForm(
                dueDays = (configs[ConfigurationName.INVOICE_DUE_DAYS]?.toInt() ?: 0),
                startNumber = (configs[ConfigurationName.INVOICE_START_NUMBER]?.toLong() ?: 0L),
            )
        )
        model.addAttribute(
            "notification",
            InvoiceNotificationForm(
                enabled = configs[ConfigurationName.INVOICE_EMAIL_ENABLED] != null,
                subject = configs[ConfigurationName.INVOICE_EMAIL_SUBJECT],
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVOICE_SETTINGS,
                title = "Invoices"
            )
        )

        return "invoices/settings/show"
    }
}

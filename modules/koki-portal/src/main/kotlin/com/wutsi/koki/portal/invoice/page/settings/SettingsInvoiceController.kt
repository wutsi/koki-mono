package com.wutsi.koki.portal.invoice.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationSettingsForm
import com.wutsi.koki.portal.invoice.form.InvoiceSettingsForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller()
@RequiresPermission(["invoice:admin"])
class SettingsInvoiceController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/invoices")
    fun show(
        model: Model
    ): String {
        val configs = service.configurations(keyword = "invoice.")
        model.addAttribute(
            "form",
            InvoiceSettingsForm(
                dueDays = (configs[ConfigurationName.INVOICE_DUE_DAYS]?.toInt() ?: 0),
                startNumber = (configs[ConfigurationName.INVOICE_START_NUMBER]?.toLong() ?: 0L),
            )
        )
        model.addAttribute(
            "openedNotification",
            InvoiceNotificationSettingsForm(
                enabled = !configs[ConfigurationName.INVOICE_EMAIL_ENABLED].isNullOrEmpty(),
                subject = configs[ConfigurationName.INVOICE_EMAIL_SUBJECT] ?: "",
                body = configs[ConfigurationName.INVOICE_EMAIL_BODY] ?: "",
            )
        )

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.INVOICE_SETTINGS,
                title = "Invoices"
            )
        )

        return "invoices/settings/show"
    }

    @GetMapping("/settings/invoices/notifications/enable")
    fun enable(
        @RequestParam status: Boolean,
    ): String {
        service.enable(status)
        return "redirect:/settings/invoices"
    }
}

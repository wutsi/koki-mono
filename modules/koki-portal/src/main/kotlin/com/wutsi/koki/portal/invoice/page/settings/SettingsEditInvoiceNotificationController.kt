package com.wutsi.koki.portal.invoice.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationSettingsForm
import com.wutsi.koki.portal.invoice.model.InvoiceNotificationType
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller()
@RequiresPermission(["invoice:admin"])
class SettingsEditInvoiceNotificationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/invoices/notifications/{type}")
    fun edit(@PathVariable type: InvoiceNotificationType, model: Model): String {
        val configs = service.configurations(keyword = "invoice.")
        val form = createForm(type, configs)
        return edit(form, model)
    }

    private fun createForm(
        type: InvoiceNotificationType,
        configs: Map<String, String>
    ): InvoiceNotificationSettingsForm {
        return InvoiceNotificationSettingsForm(
            type = type,
            enabled = when (type) {
                InvoiceNotificationType.paid -> !configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_ENABLED].isNullOrEmpty()
                InvoiceNotificationType.opened -> !configs[ConfigurationName.INVOICE_EMAIL_ENABLED].isNullOrEmpty()
            },
            subject = when (type) {
                InvoiceNotificationType.paid -> configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_SUBJECT]
                InvoiceNotificationType.opened -> configs[ConfigurationName.INVOICE_EMAIL_SUBJECT]
            },
            body = when (type) {
                InvoiceNotificationType.paid -> configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_BODY]
                InvoiceNotificationType.opened -> configs[ConfigurationName.INVOICE_EMAIL_BODY]
            },
        )
    }

    private fun edit(form: InvoiceNotificationSettingsForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.INVOICE_SETTINGS_NOTIFICATION,
                title = "Invoice Notifications"
            )
        )

        return "invoices/settings/notification"
    }

    @PostMapping("/settings/invoices/notifications/save")
    fun save(@ModelAttribute form: InvoiceNotificationSettingsForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/invoices?_toast=1&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}

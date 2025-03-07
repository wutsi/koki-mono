package com.wutsi.koki.portal.invoice.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationSettingsForm
import com.wutsi.koki.portal.invoice.form.InvoiceSettingsForm
import com.wutsi.koki.portal.invoice.model.InvoiceNotificationType
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Controller()
@RequiresPermission(["invoice:admin"])
class SettingsInvoiceController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/invoices")
    fun show(
        @RequestHeader(required = false, name = "Referer") referer: String? = null,
        @RequestParam(required = false, name = "_toast") toast: Long? = null,
        @RequestParam(required = false, name = "_ts") timestamp: Long? = null,
        @RequestParam(required = false, name = "_op") operation: String? = null,
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
            "paidNotification",
            InvoiceNotificationSettingsForm(
                enabled = !configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_ENABLED].isNullOrEmpty(),
                subject = configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_SUBJECT] ?: "",
                body = configs[ConfigurationName.INVOICE_EMAIL_RECEIPT_BODY] ?: "",
            )
        )

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.INVOICE_SETTINGS,
                title = "Invoices"
            )
        )

        loadToast(referer, toast, timestamp, operation, model)
        return "invoices/settings/show"
    }

    private fun loadToast(
        referer: String?,
        toast: Long?,
        timestamp: Long?,
        operation: String?,
        model: Model
    ) {
        if (
            toast != null &&
            canShowToasts(
                timestamp,
                referer,
                listOf(
                    "/settings/invoices",
                    "/settings/invoices/edit",
                    "/settings/invoices/notifications/paid",
                    "/settings/invoices/notifications/opened"
                )
            )
        ) {
            if (referer?.startsWith("/settings/invoices/notifications/") == true) {
                model.addAttribute("toast", "Notification updated")
            } else if (referer == "/settings/invoices") {
                if (operation == "enabled") {
                    model.addAttribute("toast", "Notification enabled")
                } else if (operation == "disabled") {
                    model.addAttribute("toast", "Notification disabled")
                }
            } else {
                model.addAttribute("toast", "Saved")
            }
        }
    }

    @GetMapping("/settings/invoices/notifications/enable")
    fun enable(
        @RequestParam type: InvoiceNotificationType,
        @RequestParam status: Boolean,
    ): String {
        service.enable(type, status)
        return "redirect:/settings/invoices?_toast=1&_ts=" + System.currentTimeMillis() +
            "&_op=" + if (status) "enabled" else "disabled"
    }
}

package com.wutsi.koki.portal.invoice.page.settings

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.form.InvoiceNotificationForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller()
@RequiresPermission(["invoice:admin"])
@RequestMapping("/settings/invoices/notifications")
class SettingsInvoiceNotificationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping
    fun edit(model: Model): String {
        val form = loadForm()
        return edit(form, model)
    }

    private fun edit(form: InvoiceNotificationForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.INVOICE_SETTINGS_NOTIFICATION,
                title = "Invoice Notifications"
            )
        )

        return "invoices/settings/notification"
    }

    private fun loadForm(): InvoiceNotificationForm {
        val configs = service.configurations(
            names = listOf(
                ConfigurationName.INVOICE_EMAIL_SUBJECT,
                ConfigurationName.INVOICE_EMAIL_BODY
            )
        )
        return InvoiceNotificationForm(
            subject = configs[ConfigurationName.INVOICE_EMAIL_SUBJECT],
            body = configs[ConfigurationName.INVOICE_EMAIL_BODY],
        )
    }

    @PostMapping("/save")
    fun save(@ModelAttribute form: InvoiceNotificationForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/invoices"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }

    @GetMapping("/enable")
    fun enable(@RequestParam status: Boolean): String {
        if (status) {
            val form = loadForm()
            if (form.subject.isNullOrEmpty() || form.body.isNullOrEmpty()) {
                return "redirect:/settings/invoices/notifications"
            }
        }
        service.enable(ConfigurationName.INVOICE_EMAIL_ENABLED, status)
        return "redirect:/settings/invoices"
    }
}

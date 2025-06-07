package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentNotificationForm
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
@RequiresPermission(["payment:admin"])
@RequestMapping("/settings/payments/notifications")
class SettingsPaymentNotificationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping
    fun edit(model: Model): String {
        return edit(loadForm(), model)
    }

    private fun loadForm(): PaymentNotificationForm {
        val configs = service.configurations(
            names = listOf(
                ConfigurationName.PAYMENT_EMAIL_SUBJECT,
                ConfigurationName.PAYMENT_EMAIL_BODY,
            )
        )
        return PaymentNotificationForm(
            subject = configs[ConfigurationName.PAYMENT_EMAIL_SUBJECT],
            body = configs[ConfigurationName.PAYMENT_EMAIL_BODY],
        )
    }

    private fun edit(form: PaymentNotificationForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page", createPageModel(
                name = PageName.PAYMENT_SETTINGS_NOTIFICATION, title = "Payment Notifications"
            )
        )

        return "payments/settings/notification"
    }

    @PostMapping("/save")
    fun save(@ModelAttribute form: PaymentNotificationForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/payments"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }

    @GetMapping("/enable")
    fun enable(
        @RequestParam status: Boolean,
    ): String {
        if (status == true) {
            val form = loadForm()
            if (form.subject.isNullOrEmpty() || form.body.isNullOrEmpty()) {
                return "redirect:/settings/payments/notifications"
            }
        }
        service.enable(ConfigurationName.PAYMENT_EMAIL_ENABLED, status)
        return "redirect:/settings/payments"
    }
}

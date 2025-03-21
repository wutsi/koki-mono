package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentNotificationSettingsForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller()
@RequiresPermission(["payment:admin"])
class SettingsEditPaymentNotificationController(
    private val service: ConfigurationService
) : AbstractPageController() {
    @GetMapping("/settings/payments/notifications")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")
        val form = PaymentNotificationSettingsForm(
            subject = configs[ConfigurationName.PAYMENT_EMAIL_SUBJECT],
            body = configs[ConfigurationName.PAYMENT_EMAIL_BODY],
        )
        return edit(form, model)
    }

    private fun edit(form: PaymentNotificationSettingsForm, model: Model): String {
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.PAYMENT_SETTINGS_NOTIFICATION,
                title = "Payment Notifications"
            )
        )

        return "payments/settings/notification"
    }

    @PostMapping("/settings/payments/notifications/save")
    fun save(@ModelAttribute form: PaymentNotificationSettingsForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/payments"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }
}

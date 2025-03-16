package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentNotificationSettingsForm
import com.wutsi.koki.portal.payment.form.PaymentSettingsForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequiresPermission(["payment:admin"])
class SettingsPaymentController(
    private val service: ConfigurationService
) : AbstractPageController() {

    @GetMapping("/settings/payments")
    fun show(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        model.addAttribute(
            "form",
            PaymentSettingsForm(
                cash = (configs[ConfigurationName.PAYMENT_METHOD_CASH_ENABLED] != null),

                check = (configs[ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED] != null),

                interac = (configs[ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED] != null),

                creditCard = (configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED] != null),
                creditCardGateway = when (configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY]?.lowercase()) {
                    "stripe" -> PaymentGateway.STRIPE
                    else -> null
                },

                paypal = (configs[ConfigurationName.PAYMENT_METHOD_PAYPAL_ENABLED] != null),

                mobile = (configs[ConfigurationName.PAYMENT_METHOD_MOBILE_ENABLED] != null),
                mobileGateway = when (configs[ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY]?.lowercase()) {
                    "flutterwave" -> PaymentGateway.FLUTTERWAVE
                    else -> null
                },
            )
        )

        model.addAttribute(
            "notification",
            PaymentNotificationSettingsForm(
                enabled = configs[ConfigurationName.PAYMENT_EMAIL_ENABLED] != null,
                subject = configs[ConfigurationName.PAYMENT_EMAIL_SUBJECT] ?: "",
                body = configs[ConfigurationName.PAYMENT_EMAIL_BODY] ?: "",
            )
        )

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.PAYMENT_SETTINGS,
                title = "Payments"
            )
        )
        return "payments/settings/show"
    }

    @GetMapping("/settings/payments/enable")
    fun enable(
        @RequestParam type: PaymentMethodType,
        @RequestParam status: Boolean,
    ): String {
        service.enable(type, status)
        return "redirect:/settings/payments"
    }

    @GetMapping("/settings/payments/notifications/enable")
    fun enable(
        @RequestParam status: Boolean,
    ): String {
        service.enable(ConfigurationName.PAYMENT_EMAIL_ENABLED, status)
        return "redirect:/settings/payments"
    }
}

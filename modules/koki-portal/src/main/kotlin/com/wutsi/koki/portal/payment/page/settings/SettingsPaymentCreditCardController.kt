package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentSettingsCreditCardForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequiresPermission(["payment:admin"])
class SettingsPaymentCreditCardController(
    private val service: ConfigurationService
) : AbstractPageController() {

    @GetMapping("/settings/payments/credit-card")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        val form = PaymentSettingsCreditCardForm(
            offlinePhoneNumber = configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER],
            offline = (configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_OFFLINE_PHONE_NUMBER] != null),
            stripeApiKey = configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY_STRIPE_API_KEY],
            gateway = configs[ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_GATEWAY]?.let { gateway ->
                try {
                    PaymentGateway.valueOf(gateway.uppercase())
                } catch (ex: Throwable) {
                    null
                }
            },
        )

        return edit(form, model)
    }

    fun edit(form: PaymentSettingsCreditCardForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.PAYMENT_SETTINGS_CREDIT_CARD,
                title = "Credit Card Payments"
            )
        )
        return "payments/settings/credit-card"
    }

    @PostMapping("/settings/payments/credit-card/save")
    fun save(@ModelAttribute form: PaymentSettingsCreditCardForm, model: Model): String {
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

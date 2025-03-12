package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentSettingsPaypalForm
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
class SettingsPaymentPaypalController(
    private val service: ConfigurationService
) : AbstractPageController() {

    @GetMapping("/settings/payments/paypal")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        val form = PaymentSettingsPaypalForm(
            clientId = configs[ConfigurationName.PAYMENT_METHOD_PAYPAL_CLIENT_ID],
            secretKey = configs[ConfigurationName.PAYMENT_METHOD_PAYPAL_SECRET_KEY],
        )

        return edit(form, model)
    }

    fun edit(form: PaymentSettingsPaypalForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.PAYMENT_SETTINGS_PAYPAL,
                title = "PayPal Payments"
            )
        )
        return "payments/settings/paypal"
    }

    @PostMapping("/settings/payments/paypal/save")
    fun save(@ModelAttribute form: PaymentSettingsPaypalForm, model: Model): String {
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

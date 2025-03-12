package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.payment.dto.PaymentGateway
import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentSettingsMobileForm
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
class SettingsPaymentMobileController(
    private val service: ConfigurationService
) : AbstractPageController() {

    @GetMapping("/settings/payments/mobile")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        val form = PaymentSettingsMobileForm(
            gateway = when (configs[ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY]?.lowercase()) {
                "flutterwave" -> PaymentGateway.FLUTTERWAVE
                else -> null
            },
            flutterwaveSecretKey = configs[ConfigurationName.PAYMENT_METHOD_MOBILE_GATEWAY_FLUTTERWAVE_SECRET_KEY],
        )

        return edit(form, model)
    }

    fun edit(form: PaymentSettingsMobileForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.PAYMENT_SETTINGS_MOBILE,
                title = "Mobile Payments"
            )
        )
        return "payments/settings/mobile"
    }

    @PostMapping("/settings/payments/mobile/save")
    fun save(@ModelAttribute form: PaymentSettingsMobileForm, model: Model): String {
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

package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentSettingsCheckForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.BusinessService
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
class SettingsPaymentCheckController(
    private val service: ConfigurationService,
    private val businessService: BusinessService,
) : AbstractPageController() {

    @GetMapping("/settings/payments/check")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        val form = PaymentSettingsCheckForm(
            payee = configs[ConfigurationName.PAYMENT_METHOD_CHECK_PAYEE] ?: getDefaultPayee(),
            instructions = configs[ConfigurationName.PAYMENT_METHOD_CHECK_INSTRUCTIONS],
        )

        return edit(form, model)
    }

    fun edit(form: PaymentSettingsCheckForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PAYMENT_SETTINGS_CHECK,
                title = "Check Payments"
            )
        )
        return "payments/settings/check"
    }

    @PostMapping("/settings/payments/check/save")
    fun save(@ModelAttribute form: PaymentSettingsCheckForm, model: Model): String {
        try {
            service.save(form)
            return "redirect:/settings/payments"
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return edit(form, model)
        }
    }

    private fun getDefaultPayee(): String? {
        return try {
            businessService.business().companyName
        } catch (ex: Exception) {
            null
        }
    }
}

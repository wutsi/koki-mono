package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentSettingsCashForm
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
class SettingsPaymentCashController(
    private val service: ConfigurationService
) : AbstractPageController() {

    @GetMapping("/settings/payments/cash")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        val form = PaymentSettingsCashForm(
            instructions = configs[ConfigurationName.PAYMENT_METHOD_CASH_INSTRUCTIONS],
        )

        return edit(form, model)
    }

    fun edit(form: PaymentSettingsCashForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PAYMENT_SETTINGS_CASH,
                title = "Cash Payments"
            )
        )
        return "payments/settings/cash"
    }

    @PostMapping("/settings/payments/cash/save")
    fun save(@ModelAttribute form: PaymentSettingsCashForm, model: Model): String {
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

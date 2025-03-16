package com.wutsi.koki.portal.payment.page.settings

import com.wutsi.koki.portal.common.model.PageModel
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.form.PaymentSettingsInteracForm
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
class SettingsPaymentInteracController(
    private val service: ConfigurationService
) : AbstractPageController() {

    @GetMapping("/settings/payments/interac")
    fun edit(model: Model): String {
        val configs = service.configurations(keyword = "payment.")

        val form = PaymentSettingsInteracForm(
            email = configs[ConfigurationName.PAYMENT_METHOD_INTERAC_EMAIL],
            question = configs[ConfigurationName.PAYMENT_METHOD_INTERAC_QUESTION],
            answer = configs[ConfigurationName.PAYMENT_METHOD_INTERAC_ANSWER],
        )

        return edit(form, model)
    }

    fun edit(form: PaymentSettingsInteracForm, model: Model): String {
        model.addAttribute("form", form)
        model.addAttribute(
            "page",
            PageModel(
                name = PageName.PAYMENT_SETTINGS_INTERAC,
                title = "Interac Payments"
            )
        )
        return "payments/settings/interac"
    }

    @PostMapping("/settings/payments/interac/save")
    fun save(@ModelAttribute form: PaymentSettingsInteracForm, model: Model): String {
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

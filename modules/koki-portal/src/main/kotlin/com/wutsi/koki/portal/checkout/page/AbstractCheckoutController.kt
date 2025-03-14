package com.wutsi.koki.portal.checkout.page

import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.portal.payment.page.AbstractPaymentController
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model

abstract class AbstractCheckoutController : AbstractPaymentController() {
    @Autowired
    protected lateinit var configurationService: ConfigurationService

    protected fun loadPaymentMethods(model: Model) {
        val config = configurationService.configurations(keyword = "payment.")
        val paymentMethodTypes = PaymentMethodType.entries
            .filter { type -> paymentMethod(type, config) != null }

        model.addAttribute("paymentMethodTypes", paymentMethodTypes)
    }

    private fun paymentMethod(type: PaymentMethodType, config: Map<String, String>): PaymentMethodType? {
        return if (type.online) {
            val name = type.name.lowercase()
            config["payment.method.$name.enabled"]?.let { type }
        } else {
            return null
        }
    }
}

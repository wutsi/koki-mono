package com.wutsi.koki.portal.checkout.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.service.TransactionService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CheckoutConfirmationController(
    private val transactionService: TransactionService,
) : AbstractCheckoutController() {
    @GetMapping("/checkout/confirmation")
    fun create(
        @RequestParam(name = "transaction-id") transactionId: String,
        model: Model,
    ): String {
        val tx = transactionService.transaction(transactionId, sync = true)
        model.addAttribute("tx", tx)

        if (tx.invoice.paid && sendEmailNotificationOnPaid() && tx.invoice.customer.email.isNotEmpty()) {
            model.addAttribute("notificationSent", true)
        }

        loadPaymentMethods(model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CHECKOUT_CONFIRMATION,
                title = "Confirmation",
            )
        )
        return "checkout/confirmation"
    }

    private fun sendEmailNotificationOnPaid(): Boolean {
        return configurationService.configurations(
            names = listOf(ConfigurationName.PAYMENT_EMAIL_ENABLED)
        ).isNotEmpty()
    }
}

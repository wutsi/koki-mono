package com.wutsi.koki.portal.checkout.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.payment.service.TransactionService
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
}

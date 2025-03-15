package com.wutsi.koki.portal.checkout.page

import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.payment.service.PaymentService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException

@Controller
class CheckoutController(
    private val invoiceService: InvoiceService,
    private val paymentService: PaymentService,
) : AbstractCheckoutController() {
    @GetMapping("/checkout/{invoice-id}")
    fun show(
        @PathVariable(name = "invoice-id") invoiceId: Long,
        model: Model,
    ): String {
        val invoice = invoiceService.invoice(id = invoiceId)
        model.addAttribute("invoice", invoice)

        loadPaymentMethods(model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.CHECKOUT,
                title = "Checkout",
            )
        )
        return "checkout/show"
    }

    @GetMapping("/checkout/prepare")
    fun prepare(
        @RequestParam(name = "invoice-id") invoiceId: Long,
        @RequestParam(name = "payment-method-type") paymentMethodType: PaymentMethodType,
        model: Model
    ): String {
        try {
            val redirectUrl = paymentService.checkout(invoiceId, paymentMethodType)
            if (redirectUrl != null) {
                return "redirect:$redirectUrl"
            } else {
                model.addAttribute("error", "Failed")
                return show(invoiceId, model)
            }
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return show(invoiceId, model)
        }
    }
}

package com.wutsi.koki.portal.payment.page

import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.invoice.model.InvoiceModel
import com.wutsi.koki.portal.invoice.service.InvoiceService
import com.wutsi.koki.portal.payment.form.PaymentForm
import com.wutsi.koki.portal.payment.service.PaymentService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.tenant.service.ConfigurationService
import com.wutsi.koki.portal.user.service.UserService
import com.wutsi.koki.tenant.dto.ConfigurationName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpClientErrorException
import java.util.Currency

@Controller
@RequiresPermission(["payment:manage"])
class CreatePaymentController(
    private val invoiceService: InvoiceService,
    private val userService: UserService,
    private val paymentService: PaymentService,
    private val configurationService: ConfigurationService,
) : AbstractPaymentController() {
    @GetMapping("/payments/create")
    fun create(
        @RequestParam(name = "invoice-id") invoiceId: Long,
        @RequestParam(required = false, name = "payment-method-type") paymentMethodType: PaymentMethodType? = null,
        model: Model,
    ): String {
        model.addAttribute("cancelUrl", "/invoices/$invoiceId?tab=invoice")
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.PAYMENT_CREATE,
                title = "New Payment",
            )
        )

        val config = configurationService.configurations(keyword = "payment.")
        if (paymentMethodType == null) {
            return selectPaymentMethod(invoiceId, model, config)
        } else {
            if (paymentMethod(paymentMethodType, config) == null) {
                return "redirect:/error/payment-not-supported?payment-method-type=$paymentMethodType"
            } else if (paymentMethodType.online) {
                val redirectUrl = paymentService.checkout(invoiceId, null, paymentMethodType)
                return "redirect:$redirectUrl"
            } else {
                val invoice = invoiceService.invoice(invoiceId, fullGraph = false)
                val form = PaymentForm(
                    invoiceId = invoiceId,
                    paymentMethodType = paymentMethodType,
                    currency = invoice.totalAmount.currency,
                    collectedById = userHolder.id()
                )
                return create(form, invoice, model)
            }
        }
    }

    @PostMapping("/payments/add-new")
    fun addNew(@ModelAttribute form: PaymentForm, model: Model): String {
        try {
            val id = paymentService.create(form)
            return "redirect:/payments/$id?_toast=$id&_ts=" + System.currentTimeMillis()
        } catch (ex: HttpClientErrorException) {
            val errorResponse = toErrorResponse(ex)
            model.addAttribute("error", errorResponse.error.code)
            return create(form, null, model)
        }
    }

    private fun selectPaymentMethod(invoiceId: Long, model: Model, config: Map<String, String>): String {
        val paymentMethodTypes = listOf(
            paymentMethod(PaymentMethodType.CASH, config),
            paymentMethod(PaymentMethodType.CHECK, config),
            paymentMethod(PaymentMethodType.INTERAC, config),
        ).filterNotNull()
        if (paymentMethodTypes.isEmpty()) {
            return "redirect:/error/payment-not-supported"
        }
        model.addAttribute("paymentMethodTypes", paymentMethodTypes)

        model.addAttribute("invoiceId", invoiceId)
        return "payments/create-payment-method"
    }

    private fun paymentMethod(type: PaymentMethodType, config: Map<String, String>): PaymentMethodType? {
        val name = when (type) {
            PaymentMethodType.CASH -> ConfigurationName.PAYMENT_METHOD_CASH_ENABLED
            PaymentMethodType.CHECK -> ConfigurationName.PAYMENT_METHOD_CHECK_ENABLED
            PaymentMethodType.INTERAC -> ConfigurationName.PAYMENT_METHOD_INTERAC_ENABLED
            PaymentMethodType.CREDIT_CARD -> ConfigurationName.PAYMENT_METHOD_CREDIT_CARD_ENABLED
            else -> null
        }

        val enabled = name?.let { config[name] }
        return if (enabled == null) {
            null
        } else {
            type
        }
    }

    private fun create(
        form: PaymentForm,
        invoice: InvoiceModel?,
        model: Model,
    ): String {
        model.addAttribute("form", form)

        val xinvoice = invoice ?: invoiceService.invoice(form.invoiceId, fullGraph = false)
        model.addAttribute("invoice", xinvoice)
        model.addAttribute("currency", Currency.getInstance(tenantHolder.get()?.currency))

        model.addAttribute(
            "collectedBy",
            form.collectedById?.let { id -> userService.get(id = id, fullGraph = false) }
        )

        return "payments/create-" + form.paymentMethodType.name.lowercase()
    }
}

package com.wutsi.koki.portal.checkout.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.InvoiceFixtures.invoice
import com.wutsi.koki.PaymentFixtures.transaction
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.payment.dto.GetTransactionResponse
import com.wutsi.koki.payment.dto.PaymentMethodType
import com.wutsi.koki.payment.dto.PrepareCheckoutRequest
import com.wutsi.koki.payment.dto.PrepareCheckoutResponse
import com.wutsi.koki.payment.dto.TransactionStatus
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class PaynowControllerTest : AbstractPageControllerTest() {
    private val paynowId = UUID.randomUUID().toString()

    @Test
    fun `paynow width credit card`() {
        navigateTo("/paynow/$paynowId.${invoice.id}")
        assertCurrentPageIs(PageName.PAYNOW)

        assertElementNotPresent(".alert-danger")
        assertElementPresent(".btn-checkout-credit_card")
        assertElementPresent(".btn-checkout-paypal")
        assertElementPresent(".btn-checkout-mobile")

        setupInvoice(InvoiceStatus.PAID)
        setupCheckoutURL("/paynow/confirmation?transaction-id=${transaction.id}")
        click(".btn-checkout-credit_card")

        val request = argumentCaptor<PrepareCheckoutRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/payments/checkout"),
            request.capture(),
            eq(PrepareCheckoutResponse::class.java)
        )
        assertEquals(invoice.id, request.firstValue.invoiceId)
        assertEquals(paynowId, request.firstValue.paynowId)
        assertEquals(PaymentMethodType.CREDIT_CARD, request.firstValue.paymentMethodType)

        assertCurrentPageIs(PageName.PAYNOW_CONFIRMATION)
        assertElementPresent(".fa-circle-check")
        assertElementNotPresent(".alert-danger")
        assertElementNotPresent(".btn-checkout-credit_card")
        assertElementNotPresent(".btn-checkout-paypal")
        assertElementNotPresent(".btn-checkout-mobile")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = "Failed", message = "Transaction failed")
        doThrow(ex).whenever(rest)
            .postForEntity(
                any<String>(),
                any(),
                eq(PrepareCheckoutResponse::class.java)
            )

        navigateTo("/paynow/$paynowId.${invoice.id}")
        assertCurrentPageIs(PageName.PAYNOW)
        click(".btn-checkout-credit_card")

        assertElementPresent(".alert-danger")
    }

    @Test
    fun `payment failed`() {
        navigateTo("/paynow/$paynowId.${invoice.id}")
        assertCurrentPageIs(PageName.PAYNOW)

        assertElementNotPresent(".alert-danger")
        assertElementPresent(".btn-checkout-credit_card")
        assertElementPresent(".btn-checkout-paypal")
        assertElementPresent(".btn-checkout-mobile")

        setupInvoice(InvoiceStatus.OPENED)
        setupTransaction(TransactionStatus.FAILED)
        setupCheckoutURL("/paynow/confirmation?transaction-id=${transaction.id}")
        click(".btn-checkout-credit_card")

        assertCurrentPageIs(PageName.PAYNOW_CONFIRMATION)
        assertElementPresent(".fa-triangle-exclamation")
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `already paid`() {
        setupInvoice(InvoiceStatus.PAID)

        navigateTo("/paynow/$paynowId.${invoice.id}")
        assertCurrentPageIs(PageName.PAYNOW)

        assertElementNotPresent(".alert-danger")
        assertElementNotPresent(".btn-checkout-credit_card")
        assertElementNotPresent(".btn-checkout-paypal")
        assertElementNotPresent(".btn-checkout-mobile")
    }

    private fun setupInvoice(status: InvoiceStatus) {
        doReturn(
            ResponseEntity(
                GetInvoiceResponse(
                    invoice.copy(
                        status = status,
                        amountDue = if (status == InvoiceStatus.PAID) 0.0 else invoice.amountDue,
                        amountPaid = if (status == InvoiceStatus.PAID) invoice.totalAmount else 0.0,
                    )
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetInvoiceResponse::class.java)
            )
    }

    private fun setupTransaction(status: TransactionStatus) {
        doReturn(
            ResponseEntity(
                GetTransactionResponse(
                    transaction.copy(
                        status = status,
                        errorCode = if (status == TransactionStatus.FAILED) ErrorCode.TRANSACTION_PAYMENT_FAILED else null,
                    )
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTransactionResponse::class.java)
            )
    }

    private fun setupCheckoutURL(path: String) {
        doReturn(
            ResponseEntity(
                PrepareCheckoutResponse(
                    transactionId = transaction.id,
                    status = transaction.status,
                    redirectUrl = "http://localhost:$port$path"
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .postForEntity(
                eq("$sdkBaseUrl/v1/payments/checkout"),
                any<PrepareCheckoutRequest>(),
                eq(PrepareCheckoutResponse::class.java)
            )
    }
}

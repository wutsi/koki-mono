package com.wutsi.koki.portal.payment.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.InvoiceFixtures.invoice
import com.wutsi.koki.UserFixtures
import com.wutsi.koki.payment.dto.CreateCashPaymentRequest
import com.wutsi.koki.payment.dto.CreateCheckPaymentRequest
import com.wutsi.koki.payment.dto.CreateInteracPaymentRequest
import com.wutsi.koki.payment.dto.CreatePaymentResponse
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test
import kotlin.test.assertEquals

class CreatePaymentControllerTest : AbstractPageControllerTest() {
    @Test
    fun cash() {
        navigateTo("/payments/create?invoice-id=${invoice.id}")

        assertCurrentPageIs(PageName.PAYMENT_CREATE)
        click(".btn-payment-cash")

        input("#amount", invoice.amountDue.toLong().toString())
        scrollToBottom()
        select2("#collectedById", UserFixtures.users[1].displayName)
        input("#description", "This is the description")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateCashPaymentRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/payments/cash"),
            request.capture(),
            eq(CreatePaymentResponse::class.java)
        )
        assertEquals(invoice.amountDue, request.firstValue.amount)
        assertEquals(invoice.currency, request.firstValue.currency)
        assertEquals(UserFixtures.users[1].id, request.firstValue.collectedById)
        assertEquals("This is the description", request.firstValue.description)

        assertCurrentPageIs(PageName.PAYMENT)
    }

    @Test
    fun `cash cancel`() {
        navigateTo("/payments/create?invoice-id=${invoice.id}")

        assertCurrentPageIs(PageName.PAYMENT_CREATE)
        click(".btn-payment-cash")

        input("#amount", invoice.amountDue.toLong().toString())
        scrollToBottom()
        select2("#collectedById", UserFixtures.users[1].displayName)
        input("#description", "This is the description")
        click(".btn-cancel", 1000)

        verify(rest, never()).postForEntity(
            eq("$sdkBaseUrl/v1/payments/cash"),
            any<CreateCashPaymentRequest>(),
            eq(CreatePaymentResponse::class.java)
        )
        assertCurrentPageIs(PageName.INVOICE)
    }

    @Test
    fun check() {
        navigateTo("/payments/create?invoice-id=${invoice.id}")

        assertCurrentPageIs(PageName.PAYMENT_CREATE)
        click(".btn-payment-check")

        input("#amount", invoice.amountDue.toLong().toString())
        scrollToBottom()
        input("#checkNumber", "123435")
        input("#bankName", "TD Bank")
        input("#description", "This is the description")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateCheckPaymentRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/payments/check"),
            request.capture(),
            eq(CreatePaymentResponse::class.java)
        )
        assertEquals(invoice.amountDue, request.firstValue.amount)
        assertEquals(invoice.currency, request.firstValue.currency)
        assertEquals("123435", request.firstValue.checkNumber)
        assertEquals("TD BANK", request.firstValue.bankName)
        assertEquals("This is the description", request.firstValue.description)

        assertCurrentPageIs(PageName.PAYMENT)
    }

    @Test
    fun `check cancel`() {
        navigateTo("/payments/create?invoice-id=${invoice.id}")

        assertCurrentPageIs(PageName.PAYMENT_CREATE)
        click(".btn-payment-check")

        input("#amount", invoice.amountDue.toLong().toString())
        scrollToBottom()
        input("#checkNumber", "123435")
        input("#bankName", "TD Bank")
        input("#description", "This is the description")
        click(".btn-cancel", 1000)

        verify(rest, never()).postForEntity(
            eq("$sdkBaseUrl/v1/payments/check"),
            any<CreateCheckPaymentRequest>(),
            eq(CreatePaymentResponse::class.java)
        )
        assertCurrentPageIs(PageName.INVOICE)
    }

    @Test
    fun interac() {
        navigateTo("/payments/create?invoice-id=${invoice.id}")

        assertCurrentPageIs(PageName.PAYMENT_CREATE)
        click(".btn-payment-interac")

        input("#amount", invoice.amountDue.toLong().toString())
        scrollToBottom()
        input("#referenceNumber", "123435")
        input("#bankName", "TD Bank")
        input("#description", "This is the description")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateInteracPaymentRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/payments/interac"),
            request.capture(),
            eq(CreatePaymentResponse::class.java)
        )
        assertEquals(invoice.amountDue, request.firstValue.amount)
        assertEquals(invoice.currency, request.firstValue.currency)
        assertEquals("123435", request.firstValue.referenceNumber)
        assertEquals("TD BANK", request.firstValue.bankName)
        assertEquals("This is the description", request.firstValue.description)

        assertCurrentPageIs(PageName.PAYMENT)
    }

    @Test
    fun `interac cancel`() {
        navigateTo("/payments/create?invoice-id=${invoice.id}")

        assertCurrentPageIs(PageName.PAYMENT_CREATE)
        click(".btn-payment-interac")

        input("#amount", invoice.amountDue.toLong().toString())
        scrollToBottom()
        input("#referenceNumber", "123435")
        input("#bankName", "TD Bank")
        input("#description", "This is the description")
        click(".btn-cancel", 1000)

        verify(rest, never()).postForEntity(
            eq("$sdkBaseUrl/v1/payments/interac"),
            any<CreateInteracPaymentRequest>(),
            eq(CreatePaymentResponse::class.java)
        )
        assertCurrentPageIs(PageName.INVOICE)
    }
}

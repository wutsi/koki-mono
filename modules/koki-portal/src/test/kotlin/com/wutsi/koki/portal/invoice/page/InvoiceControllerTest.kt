package com.wutsi.koki.portal.invoice.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures.files
import com.wutsi.koki.InvoiceFixtures.invoice
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class InvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.INVOICE)
    }

    @Test
    fun products() {
        navigateTo("/invoices/${invoice.id}?tab=invoice-product")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementCount("tr.invoice-item", invoice.items.size)
        assertElementCount("tr.invoice-tax", 2)
    }

    @Test
    fun file() {
        navigateTo("/invoices/${invoice.id}?tab=file")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementCount("tr.file", files.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/invoices/${invoice.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `show - without permission invoice`() {
        setUpUserWithoutPermissions(listOf("invoice"))

        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission invoice-manage`() {
        setUpUserWithoutPermissions(listOf("invoice:manage"))

        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementNotPresent(".btn-approve")
    }

    @Test
    fun `show - without permission invoice-void`() {
        setUpUserWithoutPermissions(listOf("invoice:void"))

        navigateTo("/invoices/${invoice.id}")

        assertCurrentPageIs(PageName.INVOICE)
        assertElementNotPresent(".btn-void")
    }

    @Test
    fun approve() {
        setupDraftInvoice()

        navigateTo("/invoices/${invoice.id}")
        click(".btn-approve")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        val request = argumentCaptor<UpdateInvoiceStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/invoices/${invoice.id}/statuses"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals(InvoiceStatus.OPENED, request.firstValue.status)
        assertEquals(null, request.firstValue.comment)

        assertCurrentPageIs(PageName.INVOICE)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `approve - without permission invoice-manage`() {
        setupDraftInvoice()
        setUpUserWithoutPermissions(listOf("invoice:manage"))

        navigateTo("/invoices/${invoice.id}/approve")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun void() {
        navigateTo("/invoices/${invoice.id}")
        click(".btn-void")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        val request = argumentCaptor<UpdateInvoiceStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/invoices/${invoice.id}/statuses"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals(InvoiceStatus.VOIDED, request.firstValue.status)
        assertEquals(null, request.firstValue.comment)

        assertCurrentPageIs(PageName.INVOICE)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `void - without permission invoice-manage`() {
        setupDraftInvoice()
        setUpUserWithoutPermissions(listOf("invoice:manage"))

        navigateTo("/invoices/${invoice.id}/void")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun download() {
        navigateTo("/invoices/${invoice.id}")
        assertCurrentPageIs(PageName.INVOICE)
        click(".btn-download")
    }

    @Test
    fun `download - without permission invoice`() {
        setUpUserWithoutPermissions(listOf("invoice"))

        navigateTo("/invoices/i${invoice.id}/${UUID.randomUUID()}.pdf")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    private fun setupDraftInvoice() {
        doReturn(
            ResponseEntity(
                GetInvoiceResponse(invoice.copy(status = InvoiceStatus.DRAFT)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetInvoiceResponse::class.java)
            )
    }
}

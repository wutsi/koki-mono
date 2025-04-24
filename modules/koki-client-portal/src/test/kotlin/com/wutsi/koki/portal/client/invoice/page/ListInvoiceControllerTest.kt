package com.wutsi.koki.portal.client.invoice.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.InvoiceFixtures.invoices
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListInvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/invoices")

        assertCurrentPageIs(PageName.INVOICE_LIST)
        assertElementCount("tr.invoice", invoices.size)
        assertElementCount(".btn-download", invoices.size)
        assertElementCount(".btn-paynow", invoices.filter { invoice ->
            invoice.status == InvoiceStatus.OPENED
        }.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<InvoiceSummary>()
        repeat(20) {
            entries.add(invoices[0].copy())
        }

        doReturn(
            ResponseEntity(
                SearchInvoiceResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchInvoiceResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchInvoiceResponse(invoices),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        navigateTo("/invoices")

        assertCurrentPageIs(PageName.INVOICE_LIST)
        assertElementCount("tr.invoice", entries.size)

        scrollToBottom()

        click("#invoice-load-more button")
        assertElementCount("tr.invoice", 2 * entries.size)

        scrollToBottom()

        click("#invoice-load-more button")
        assertElementCount("tr.invoice", 2 * entries.size + invoices.size)
    }

    @Test
    fun `no access to module`() {
        disableModule("invoice")

        navigateTo("/invoices")

        assertCurrentPageIs(PageName.ERROR_403)
    }
}

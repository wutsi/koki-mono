package com.wutsi.koki.portal.invoice.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.InvoiceFixtures.invoices
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListInvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/invoices")
        assertCurrentPageIs(PageName.INVOICE_LIST)
        assertElementCount("tr.invoice", invoices.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<InvoiceSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(invoices[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchInvoiceResponse(entries),
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
        click("#invoice-load-more a", 1000)
        assertElementCount("tr.invoice", 2 * entries.size)
    }

    @Test
    fun show() {
        navigateTo("/invoices")
        click("tr.invoice a")
        assertCurrentPageIs(PageName.INVOICE)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/invoices")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `list - without permission invoice`() {
        setUpUserWithoutPermissions(listOf("invoice"))

        navigateTo("/invoices")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission invoice-manage`() {
        setUpUserWithoutPermissions(listOf("invoice:manage"))

        navigateTo("/invoices")

        assertCurrentPageIs(PageName.INVOICE_LIST)
//        assertElementNotPresent(".btn-create")
    }
}

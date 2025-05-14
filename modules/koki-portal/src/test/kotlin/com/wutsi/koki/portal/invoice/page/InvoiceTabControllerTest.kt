package com.wutsi.koki.portal.invoice.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures
import com.wutsi.koki.InvoiceFixtures.invoices
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.invoice.dto.InvoiceSummary
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class InvoiceTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun `list - account`() {
        navigateTo("/invoices/tab?test-mode=true&owner-type=ACCOUNT&owner-id=" + AccountFixtures.account.id)
        assertElementCount(".tab-invoices tr.invoice", invoices.size)
    }

    @Test
    fun `list - tax`() {
        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertElementCount(".tab-invoices tr.invoice", invoices.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<InvoiceSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(invoices[0].copy(id = seed++))
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

        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertElementCount("tr.invoice", entries.size)

        scrollToBottom()
        click("#invoice-load-more a", 1000)
        assertElementCount("tr.invoice", 2 * entries.size)
    }

    @Test
    fun `list - without permission invoice`() {
        setUpUserWithoutPermissions(listOf("invoice"))

        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

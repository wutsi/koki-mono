package com.wutsi.koki.portal.client.invoice.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.InvoiceFixtures.invoice
import com.wutsi.koki.portal.client.common.page.PageName
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class DownloadInvoiceControllerTest : AbstractPageControllerTest() {
    @Test
    fun download() {
        navigateTo("/invoices/${invoice.id}/download")
    }

    @Test
    fun `not owner`() {
        doReturn(
            ResponseEntity(
                GetInvoiceResponse(invoice.copy(customer = invoice.customer.copy(accountId = 9999))),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetInvoiceResponse::class.java)
            )

        navigateTo("/invoices/${invoice.id}/download")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `not found`() {
        doThrow(
            createHttpClientErrorException(404, ErrorCode.INVOICE_NOT_FOUND)
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetInvoiceResponse::class.java)
            )

        navigateTo("/invoices/${invoice.id}/download")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `invalid id`() {
        navigateTo("/invoice/four-hundred/download")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/invoices/${invoice.id}/download")

        assertCurrentPageIs(PageName.LOGIN)
    }
}

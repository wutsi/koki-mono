package com.wutsi.koki.portal.invoice.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures
import com.wutsi.koki.InvoiceFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tax.dto.GetTaxResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class InvoiceTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun `list - account`() {
        navigateTo("/invoices/tab?test-mode=true&owner-type=ACCOUNT&owner-id=" + AccountFixtures.account.id)
        assertElementCount(".tab-invoices tr.invoice", InvoiceFixtures.invoices.size)
    }

    @Test
    fun `list - tax`() {
        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertElementCount(".tab-invoices tr.invoice", InvoiceFixtures.invoices.size)
    }

    @Test
    fun `list - tax without product and no invoices`() {
        // GIVEN
        doReturn( // product with no products
            ResponseEntity(
                GetTaxResponse(TaxFixtures.tax.copy(productCount = 0)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        doReturn( // no invoices
            ResponseEntity(
                SearchInvoiceResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertElementNotPresent(".tab-invoices .btn-create-invoice")
    }

    @Test
    fun `list - with voided invoices`() {
        // GIVEN
        doReturn( // no invoices
            ResponseEntity(
                SearchInvoiceResponse(InvoiceFixtures.invoices.map { it.copy(status = InvoiceStatus.VOIDED) }),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertElementPresent(".tab-invoices .btn-create-invoice")
    }

    @Test
    fun `create tax invoice`() {
        // GIVEN
        doReturn( // no invoices
            ResponseEntity(
                SearchInvoiceResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        // WHEN
        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertElementCount("tr.invoice", 0)

        click(".tab-invoices .btn-create-invoice")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        val request = argumentCaptor<CreateInvoiceRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/invoices"),
            request.capture(),
            eq(CreateInvoiceResponse::class.java)
        )
        assertEquals(TaxFixtures.tax.id, request.firstValue.taxId)
        assertEquals(null, request.firstValue.orderId)
        assertEquals(TaxFixtures.tax.accountId, request.firstValue.customerAccountId)
        assertEquals(AccountFixtures.account.name, request.firstValue.customerName)
        assertEquals(AccountFixtures.account.email, request.firstValue.customerEmail)
        assertEquals(AccountFixtures.account.phone, request.firstValue.customerPhone)
        assertEquals(AccountFixtures.account.mobile, request.firstValue.customerMobile)

        assertEquals(AccountFixtures.account.shippingAddress?.street, request.firstValue.shippingStreet)
        assertEquals(AccountFixtures.account.shippingAddress?.postalCode, request.firstValue.shippingPostalCode)
        assertEquals(AccountFixtures.account.shippingAddress?.country, request.firstValue.shippingCountry)
        assertEquals(AccountFixtures.account.shippingAddress?.cityId, request.firstValue.shippingCityId)

        assertEquals(AccountFixtures.account.billingAddress?.street, request.firstValue.billingStreet)
        assertEquals(AccountFixtures.account.billingAddress?.postalCode, request.firstValue.billingPostalCode)
        assertEquals(AccountFixtures.account.billingAddress?.country, request.firstValue.billingCountry)
        assertEquals(AccountFixtures.account.billingAddress?.cityId, request.firstValue.billingCityId)

        assertEquals(TaxFixtures.taxProducts.size, request.firstValue.items.size)
        var i = 0
        request.firstValue.items.forEach { item ->
            assertEquals(TaxFixtures.taxProducts[i].productId, item.productId, "item#$i")
            assertEquals(TaxFixtures.taxProducts[i].unitPriceId, item.unitPriceId, "item#$i")
            assertEquals(TaxFixtures.taxProducts[i].unitPrice, item.unitPrice, "item#$i")
            assertEquals(TaxFixtures.taxProducts[i].description, item.description, "item#$i")

            i++
        }

        assertCurrentPageIs(PageName.INVOICE)
        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `list - without permission invoice-manage`() {
        setUpUserWithoutPermissions(listOf("invoice:manage"))

        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)

        assertElementNotPresent(".btn-create-invoice")
    }

    @Test
    fun `list - without permission invoice`() {
        setUpUserWithoutPermissions(listOf("invoice"))

        navigateTo("/invoices/tab?test-mode=true&owner-type=TAX&owner-id=" + TaxFixtures.tax.id)
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

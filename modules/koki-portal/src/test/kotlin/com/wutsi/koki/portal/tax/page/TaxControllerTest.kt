package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures
import com.wutsi.koki.EmailFixtures
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.InvoiceFixtures
import com.wutsi.koki.NoteFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tax.dto.GetTaxResponse
import com.wutsi.koki.tax.dto.SearchTaxProductResponse
import com.wutsi.koki.tax.dto.TaxStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class TaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/taxes/${tax.id}")
        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/${tax.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/taxes/${tax.id}")
        assertCurrentPageIs(PageName.TAX_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `dismiss delete`() {
        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `error on delete`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/taxes/${tax.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.TAX)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun taxProducts() {
        navigateTo("/taxes/${tax.id}?tab=tax-product")

        Thread.sleep(1000)
        assertElementCount(".tab-tax-products tr.tax-product", TaxFixtures.taxProducts.size)
    }

    @Test
    fun files() {
        navigateTo("/taxes/${tax.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files tr.file", FileFixtures.files.size)
    }

    @Test
    fun metric() {
        navigateTo("/taxes/${tax.id}?tab=metric")
    }

    @Test
    fun invoices() {
        navigateTo("/taxes/${tax.id}?tab=invoice")

        Thread.sleep(1000)
        assertElementCount(".tab-invoices tr.invoice", InvoiceFixtures.invoices.size)
    }

    @Test
    fun notes() {
        navigateTo("/taxes/${tax.id}?tab=note")

        Thread.sleep(1000)
        assertElementCount(".tab-notes .note", NoteFixtures.notes.size)
    }

    @Test
    fun emails() {
        navigateTo("/taxes/${tax.id}?tab=email")

        Thread.sleep(1000)
        assertElementCount(".tab-emails .email", EmailFixtures.emails.size)
    }

    @Test
    fun assignee() {
        navigateTo("/taxes/${tax.id}")

        click(".btn-assignee")
        assertCurrentPageIs(PageName.TAX_ASSIGNEE)
    }

    @Test
    fun status() {
        navigateTo("/taxes/${tax.id}")

        click(".btn-status")
        assertCurrentPageIs(PageName.TAX_STATUS)
    }

    @Test
    fun `show - without permission tax`() {
        setUpUserWithoutPermissions(listOf("tax"))

        navigateTo("/taxes/${tax.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes/${tax.id}")
        assertElementNotPresent(".tax-summary .btn-edit")
        assertElementNotPresent(".tax-summary .btn-status")
        assertElementNotPresent(".tax-summary .btn-assignee")
    }

    @Test
    fun `show - done`() {
        doReturn(
            ResponseEntity(
                GetTaxResponse(tax.copy(status = TaxStatus.DONE)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes/${tax.id}")
        assertElementNotPresent(".tax-summary .btn-edit")
        assertElementNotPresent(".tax-summary .btn-status")
        assertElementNotPresent(".tax-summary .btn-assignee")
        assertElementNotPresent(".tax-summary .btn-delete")
    }

    @Test
    fun `show - without permission tax-delete`() {
        setUpUserWithoutPermissions(listOf("tax:delete"))

        navigateTo("/taxes/${tax.id}")
        assertElementNotPresent(".tax-summary .btn-delete")
    }

    @Test
    fun `delete - without permission tax-delete`() {
        setUpUserWithoutPermissions(listOf("tax:delete"))

        navigateTo("/taxes/${tax.id}/delete")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `delete - without permission product`() {
        setUpUserWithoutPermissions(listOf("product"))

        navigateTo("/taxes/${tax.id}")
        assertElementNotPresent("#pills-tax-product")
        assertElementNotPresent("#pills-tax-product-tab")
    }

    @Test
    fun `create invoice`() {
        // GIVEN
        doReturn(
            ResponseEntity(
                GetTaxResponse(tax.copy(status = TaxStatus.DONE)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                SearchInvoiceResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        // GIVEN
        navigateTo("/taxes/${tax.id}")
        click(".btn-create-invoice")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        // THEN
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
        assertEquals(
            "${AccountFixtures.account.language}_${AccountFixtures.account.shippingAddress?.country}",
            request.firstValue.locale
        )

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
            assertEquals(TaxFixtures.taxProducts[i].quantity, item.quantity, "item#$i")
            i++
        }

        assertCurrentPageIs(PageName.INVOICE)
        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `create invoice with no product`() {
        // GIVEN
        doReturn(
            ResponseEntity(
                GetTaxResponse(tax.copy(status = TaxStatus.DONE)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                SearchTaxProductResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchTaxProductResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                SearchInvoiceResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchInvoiceResponse::class.java)
            )

        // GIVEN
        navigateTo("/taxes/${tax.id}")
        click(".btn-create-invoice")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        // THEN
        verify(rest, never()).postForEntity(
            eq("$sdkBaseUrl/v1/invoices"),
            any(),
            eq(CreateInvoiceResponse::class.java)
        )

        assertCurrentPageIs(PageName.TAX)
        assertElementPresent(".alert-danger")
    }
}

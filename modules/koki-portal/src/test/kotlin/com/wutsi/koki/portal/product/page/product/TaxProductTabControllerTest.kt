package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures
import com.wutsi.koki.ProductFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.invoice.dto.CreateInvoiceRequest
import com.wutsi.koki.invoice.dto.CreateInvoiceResponse
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tax.dto.CreateTaxProductRequest
import com.wutsi.koki.tax.dto.CreateTaxProductResponse
import com.wutsi.koki.tax.dto.UpdateTaxProductRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class TaxProductTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)
        assertElementCount("tr.tax-product", TaxFixtures.taxProducts.size)
    }

    @Test
    fun delete() {
        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        val id = TaxFixtures.taxProduct.id
        click("#tax-product-$id .btn-delete")
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/tax-products/$id")
    }

    @Test
    fun edit() {
        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        val id = TaxFixtures.taxProduct.id
        click("#tax-product-$id .btn-edit")

        Thread.sleep(1000)
        assertElementVisible("#koki-modal")

        input("#description", "This is a product")
        select("#unitPriceId", 2)
        input("#quantity", "4")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateTaxProductRequest>()
        verify(rest).postForEntity(eq("$sdkBaseUrl/v1/tax-products/$id"), request.capture(), eq(Any::class.java))
        assertEquals(ProductFixtures.prices[2].id, request.firstValue.unitPriceId)
        assertEquals(4, request.firstValue.quantity)
        assertEquals("This is a product", request.firstValue.description)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun add() {
        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        click(".tab-tax-products .btn-add-product", 1000)
        assertElementVisible("#koki-modal")

        select2("#productId", "${ProductFixtures.products[0].code} - ${ProductFixtures.products[0].name}")
        Thread.sleep(1000)
        select("#unitPriceId", 2)
        input("#quantity", "4")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateTaxProductRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/tax-products"),
            request.capture(),
            eq(CreateTaxProductResponse::class.java)
        )
        assertEquals(ProductFixtures.products[0].id, request.firstValue.productId)
        assertEquals(ProductFixtures.prices[2].id, request.firstValue.unitPriceId)
        assertEquals(4, request.firstValue.quantity)
        assertEquals(null, request.firstValue.description)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun `list - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        assertElementNotPresent(".btn-add-product")
        assertElementNotPresent(".btn-delete")
        assertElementNotPresent(".btn-edit")
    }

    @Test
    fun createInvoice() {
        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        click(".tab-tax-products .btn-create-invoice")

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
}

package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.ProductFixtures
import com.wutsi.koki.TaxFixtures
import com.wutsi.koki.portal.AbstractPageControllerTest
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

        assertElementPresent(".btn-add-product")
        assertElementPresent(".btn-edit")
        assertElementPresent(".btn-delete")
        assertElementCount("tr.tax-product", TaxFixtures.taxProducts.size)
    }

    @Test
    fun `read only`() {
        navigateTo("/tax-products/tab?test-mode=true&read-only=true&tax-id=" + TaxFixtures.tax.id)

        assertElementNotPresent(".btn-add-product")
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-delete")
        assertElementCount("tr.tax-product", TaxFixtures.taxProducts.size)
    }

    @Test
    fun delete() {
        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        val id = TaxFixtures.taxProduct.id
        click("#tax-product-$id .btn-delete", 100)
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
        click("#tax-product-$id .btn-edit", 1000)

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
    fun `list - without permission tax`() {
        setUpUserWithoutPermissions(listOf("tax"))

        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ProductFixtures
import com.wutsi.koki.TaxFixtures
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

        Thread.sleep(1000)
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

        click(".tab-tax-products .btn-add-product")

        Thread.sleep(1000)
        assertElementVisible("#koki-modal")
    }

    @Test
    fun `list - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/tax-products/tab?test-mode=true&tax-id=" + TaxFixtures.tax.id)

        assertElementNotPresent(".btn-add-product")
        assertElementNotPresent(".btn-delete")
        assertElementNotPresent(".btn-edit")
    }
}

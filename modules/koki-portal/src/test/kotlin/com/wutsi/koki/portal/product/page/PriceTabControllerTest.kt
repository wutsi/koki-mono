package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.ProductFixtures.prices
import com.wutsi.koki.ProductFixtures.product
import com.wutsi.koki.TenantFixtures.tenants
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.product.dto.CreatePriceRequest
import com.wutsi.koki.product.dto.CreatePriceResponse
import com.wutsi.koki.product.dto.UpdatePriceRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/prices/tab?test-mode=true&product-id=${product.id}")

        assertElementCount("tr.price", prices.size)
    }

    @Test
    fun create() {
        navigateTo("/prices/tab?test-mode=true&product-id=${product.id}")

        click(".btn-add-price", 1000)

        assertElementVisible("#koki-modal")
        input("#name", "Default Price")
        input("#amount", "750")
        select("#active", 1)
        click("#btn-price-submit", 1000)

        val request = argumentCaptor<CreatePriceRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/prices"),
            request.capture(),
            eq(CreatePriceResponse::class.java)
        )

        assertEquals(product.id, request.firstValue.productId)
        assertEquals("Default Price", request.firstValue.name)
        assertEquals(750.0, request.firstValue.amount)
        assertEquals(tenants[0].currency, request.firstValue.currency)
        assertEquals(false, request.firstValue.active)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun edit() {
        navigateTo("/prices/tab?test-mode=true&product-id=${product.id}")

        click(".btn-edit", 1000)

        assertElementVisible("#koki-modal")
        input("#name", "Default Price")
        input("#amount", "750")
        select("#active", 1)
        click("#btn-price-submit", 1000)

        val request = argumentCaptor<UpdatePriceRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/prices/${prices[0].id}"),
            request.capture(),
            eq(Any::class.java)
        )

        assertEquals("Default Price", request.firstValue.name)
        assertEquals(750.0, request.firstValue.amount)
        assertEquals(tenants[0].currency, request.firstValue.currency)
        assertEquals(false, request.firstValue.active)

        assertElementNotVisible("#koki-modal")
    }

    @Test
    fun delete() {
        navigateTo("/prices/tab?test-mode=true&product-id=${product.id}")

        click(".btn-delete", 1000)
        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        Thread.sleep(1000)
        verify(rest).delete("$sdkBaseUrl/v1/prices/${prices[0].id}")
    }

    @Test
    fun `list - without permission product-manage`() {
        setUpUserWithoutPermissions(listOf("product:manage"))

        navigateTo("/prices/tab?test-mode=true&product-id=${product.id}")
        assertElementNotPresent(".btn-add-price")
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-delete")
    }
}

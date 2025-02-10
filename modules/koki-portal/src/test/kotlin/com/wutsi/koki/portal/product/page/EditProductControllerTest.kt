package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.ProductFixtures.product
import com.wutsi.koki.RefDataFixtures
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.product.dto.ProductType
import com.wutsi.koki.product.dto.UpdateProductRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class EditProductControllerTest : AbstractPageControllerTest() {
    private fun inputFields(type: Int = 2) {
        input("#name", "Product A")
        select("#type", 2)
        input("#code", "PA")
        input("#description", "This is the description of the product")
        select("#active", 1)
        scrollToBottom()

        if (type == 1) {
            select("#unitId", 2)
            input("#quantity", "4")
        }
    }

    @Test
    fun `edit service`() {
        navigateTo("/products/${product.id}/edit")

        assertCurrentPageIs(PageName.PRODUCT_EDIT)

        inputFields(1)
        click("button[type=submit]")

        val request = argumentCaptor<UpdateProductRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/products/${product.id}"), request.capture(), eq(Any::class.java)
        )
        assertEquals("Product A", request.firstValue.name)
        assertEquals(ProductType.DIGITAL, request.firstValue.type)
        assertEquals("PA", request.firstValue.code)
        assertEquals("This is the description of the product", request.firstValue.description)
        assertEquals(false, request.firstValue.active)
        assertEquals(RefDataFixtures.units[1].id, request.firstValue.unitId)
        assertEquals(4, request.firstValue.quantity)

        assertCurrentPageIs(PageName.PRODUCT)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun cancel() {
        navigateTo("/products/${product.id}/edit")

        assertCurrentPageIs(PageName.PRODUCT_EDIT)

        inputFields()
        click(".btn-cancel")

        assertCurrentPageIs(PageName.PRODUCT_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(), any<UpdateProductRequest>(), eq(Any::class.java)
        )

        navigateTo("/products/${product.id}/edit")

        inputFields()
        click("button[type=submit]")

        assertCurrentPageIs(PageName.PRODUCT_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/products/${product.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `create - without permission product-manage`() {
        setUpUserWithoutPermissions(listOf("product:manage"))

        navigateTo("/products/${product.id}/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

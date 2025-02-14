package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.FileFixtures
import com.wutsi.koki.ProductFixtures
import com.wutsi.koki.ProductFixtures.product
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class ProductControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/products/${product.id}")

        assertCurrentPageIs(PageName.PRODUCT)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/products/${product.id}")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun delete() {
        navigateTo("/products/${product.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/products/${product.id}")
        assertCurrentPageIs(PageName.PRODUCT_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `delete - dismiss`() {
        navigateTo("/products/${product.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.dismiss()
        driver.switchTo().parentFrame()

        verify(rest, never()).delete(any<String>())
        assertCurrentPageIs(PageName.PRODUCT)
    }

    @Test
    fun `delete - error`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/products/${product.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()

        assertCurrentPageIs(PageName.PRODUCT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun edit() {
        navigateTo("/products/${product.id}")
        click(".btn-edit")
        assertCurrentPageIs(PageName.PRODUCT_EDIT)
    }

    @Test
    fun prices() {
        navigateTo("/products/${product.id}?tab=price")

        Thread.sleep(1000)
        assertElementCount(".tab-prices .price", ProductFixtures.prices.size)
    }

    @Test
    fun files() {
        navigateTo("/products/${product.id}?tab=file")

        Thread.sleep(1000)
        assertElementCount(".tab-files .file", FileFixtures.files.size)
    }

    @Test
    fun `show - without permission product`() {
        setUpUserWithoutPermissions(listOf("product"))

        navigateTo("/products/${product.id}")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `show - without permission product-manage`() {
        setUpUserWithoutPermissions(listOf("product:manage"))

        navigateTo("/products/${product.id}")

        assertCurrentPageIs(PageName.PRODUCT)
        assertElementNotPresent(".product-summary .btn-edit")
    }

    @Test
    fun `show - without permission product-delete`() {
        setUpUserWithoutPermissions(listOf("product:delete"))

        navigateTo("/products/${product.id}")

        assertCurrentPageIs(PageName.PRODUCT)
        assertElementNotPresent(".product-summary .btn-delete")
    }

    @Test
    fun `delete - without permission product-delete`() {
        setUpUserWithoutPermissions(listOf("product:delete"))

        navigateTo("/products/${product.id}/delete")

        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

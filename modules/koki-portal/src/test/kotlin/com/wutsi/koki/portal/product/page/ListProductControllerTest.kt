package com.wutsi.koki.portal.product.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ProductFixtures.products
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.product.dto.ProductSummary
import com.wutsi.koki.product.dto.SearchProductResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListProductControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/products")

        assertCurrentPageIs(PageName.PRODUCT_LIST)
        assertElementCount("tr.product", products.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<ProductSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(products[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchProductResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchProductResponse::class.java)
            )

        navigateTo("/products")

        assertCurrentPageIs(PageName.PRODUCT_LIST)
        assertElementCount("tr.product", entries.size)

        scrollToBottom()
        click("#product-load-more a", 1000)
        assertElementCount("tr.product", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/products")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun show() {
        navigateTo("/products")
        click("tr.product a")
        assertCurrentPageIs(PageName.PRODUCT)
    }

    @Test
    fun create() {
        navigateTo("/products")
        click(".btn-create")
        assertCurrentPageIs(PageName.PRODUCT_CREATE)
    }

    @Test
    fun `list - without permission product`() {
        setupUserWithoutPermissions(listOf("product"))

        navigateTo("/products")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `list - without permission product-manage`() {
        setupUserWithoutPermissions(listOf("product:manage"))

        navigateTo("/products")

        assertCurrentPageIs(PageName.PRODUCT_LIST)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
    }
}

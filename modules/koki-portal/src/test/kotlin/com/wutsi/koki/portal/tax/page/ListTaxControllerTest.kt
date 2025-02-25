package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.taxes
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tax.dto.SearchTaxResponse
import com.wutsi.koki.tax.dto.TaxSummary
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class ListTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/taxes")

        assertCurrentPageIs(PageName.TAX_LIST)
        assertElementCount("tr.tax", taxes.size)
    }

    @Test
    fun loadMore() {
        var entries = mutableListOf<TaxSummary>()
        var seed = System.currentTimeMillis()
        repeat(20) {
            entries.add(taxes[0].copy(id = ++seed))
        }
        doReturn(
            ResponseEntity(
                SearchTaxResponse(entries),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                anyOrNull<String>(),
                eq(SearchTaxResponse::class.java)
            )

        navigateTo("/taxes")

        assertCurrentPageIs(PageName.TAX_LIST)
        assertElementCount("tr.tax", entries.size)

        scrollToBottom()
        click("#tax-load-more a", 1000)
        assertElementCount("tr.tax", 2 * entries.size)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun show() {
        navigateTo("/taxes")
        click(".btn-view")
        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun create() {
        navigateTo("/taxes")
        click(".btn-create")
        assertCurrentPageIs(PageName.TAX_CREATE)
    }

    @Test
    fun edit() {
        navigateTo("/taxes")
        click(".btn-edit")
        assertCurrentPageIs(PageName.TAX_EDIT)
    }

    @Test
    fun `list - without permission tax`() {
        setUpUserWithoutPermissions(listOf("tax"))

        navigateTo("/taxes")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }

    @Test
    fun `list - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes")

        assertCurrentPageIs(PageName.TAX_LIST)
        assertElementNotPresent(".btn-edit")
        assertElementNotPresent(".btn-create")
    }
}

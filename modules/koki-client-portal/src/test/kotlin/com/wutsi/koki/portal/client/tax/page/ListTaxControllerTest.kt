package com.wutsi.koki.portal.client.tax.page

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.TaxFixtures.tax
import com.wutsi.koki.portal.client.TaxFixtures.taxes
import com.wutsi.koki.portal.client.common.page.PageName
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
        repeat(20) {
            entries.add(taxes[0].copy())
        }

        doReturn(
            ResponseEntity(
                SearchTaxResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchTaxResponse(entries),
                HttpStatus.OK,
            )
        ).doReturn(
            ResponseEntity(
                SearchTaxResponse(taxes),
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

        click("#tax-load-more button")
        assertElementCount("tr.tax", 2 * entries.size)

        scrollToBottom()

        click("#tax-load-more button")
        assertElementCount("tr.tax", 2 * entries.size + taxes.size)
    }

    @Test
    fun `no access to module`() {
        disableModule("tax")

        navigateTo("/taxes")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes")
        assertCurrentPageIs(PageName.LOGIN)
    }
}

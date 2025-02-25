package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tax.dto.UpdateTaxRequest
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class EditTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/taxes/${tax.id}/edit")

        assertCurrentPageIs(PageName.TAX_EDIT)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        val request = argumentCaptor<UpdateTaxRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/taxes/${tax.id}"),
            request.capture(),
            eq(Any::class.java),
        )
        val tax = request.firstValue
        assertEquals(LocalDate.now().year - 3, tax.fiscalYear)
        assertEquals(110L, tax.taxTypeId)
        assertEquals("This is a nice description", tax.description)

        assertCurrentPageIs(PageName.TAX)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun cancel() {
        navigateTo("/taxes/${tax.id}/edit")

        assertCurrentPageIs(PageName.TAX_EDIT)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click(".btn-cancel")

        assertCurrentPageIs(PageName.TAX_LIST)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<UpdateTaxRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/taxes/${tax.id}/edit")

        assertCurrentPageIs(PageName.TAX_EDIT)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.TAX_EDIT)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/${tax.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `edit - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes/${tax.id}/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

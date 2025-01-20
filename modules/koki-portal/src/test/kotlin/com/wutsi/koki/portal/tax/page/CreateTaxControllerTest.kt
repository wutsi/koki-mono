package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.TaxFixtures.taxTypes
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tax.dto.CreateTaxRequest
import com.wutsi.koki.tax.dto.CreateTaxResponse
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/taxes/create?account-id=${account.id}")

        assertCurrentPageIs(PageName.TAX_CREATE)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        SimpleDateFormat("yyyy-MM-dd")
        val request = argumentCaptor<CreateTaxRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/taxes"),
            request.capture(),
            eq(CreateTaxResponse::class.java),
        )
        val tax = request.firstValue
        assertEquals(LocalDate.now().year - 2, tax.fiscalYear)
        assertEquals(taxTypes[2].id, tax.taxTypeId)
//        assertEquals("2020-12-11", fmt.format(tax.startAt))
//        assertEquals("2020-12-21", fmt.format(tax.dueAt))
        assertEquals("This is a nice description", tax.description)

        assertCurrentPageIs(PageName.TAX_SAVED)
        click(".btn-ok")
        assertCurrentPageIs(PageName.TAX_LIST)
    }

    @Test
    fun `create new`() {
        navigateTo("/taxes/create")

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.TAX_SAVED)
        click(".btn-create")
        assertCurrentPageIs(PageName.TAX_CREATE)
    }

    @Test
    fun cancel() {
        navigateTo("/taxes/create")

        assertCurrentPageIs(PageName.TAX_CREATE)

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
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.FORM_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<CreateTaxRequest>(),
            eq(CreateTaxResponse::class.java),
        )

        navigateTo("/taxes/create")

        assertCurrentPageIs(PageName.TAX_CREATE)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.TAX_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/create")
        assertCurrentPageIs(PageName.LOGIN)
    }
}

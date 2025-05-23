package com.wutsi.koki.portal.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AccountFixtures
import com.wutsi.koki.AccountFixtures.account
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
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
        click("button[type=submit]", 1000)

        SimpleDateFormat("yyyy-MM-dd")
        val request = argumentCaptor<CreateTaxRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/taxes"),
            request.capture(),
            eq(CreateTaxResponse::class.java),
        )
        val tax = request.firstValue
        assertEquals(LocalDate.now().year - 3, tax.fiscalYear)
        assertEquals(110L, tax.taxTypeId)
//        assertEquals("2020-12-11", fmt.format(tax.startAt))
//        assertEquals("2020-12-21", fmt.format(tax.dueAt))
        assertEquals("This is a nice description", tax.description)

        assertCurrentPageIs(PageName.TAX)
        assertElementVisible("#koki-toast")
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
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<CreateTaxRequest>(),
            eq(CreateTaxResponse::class.java),
        )

        navigateTo("/taxes/create")

        assertCurrentPageIs(PageName.TAX_CREATE)

        select("#fiscalYear", 2)
        select("#taxTypeId", 3)
        select2("#accountId", AccountFixtures.accounts[1].name)
        scrollToBottom()
        input("#startAt", "2020\t1211")
        input("#dueAt", "2020\t1221")
        input("#description", "This is a nice description")
        click("button[type=submit]", 1000)

        assertCurrentPageIs(PageName.TAX_CREATE)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/create")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `create - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

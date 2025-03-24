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
import com.wutsi.koki.tax.dto.TaxStatus
import com.wutsi.koki.tax.dto.UpdateTaxStatusRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class TaxStatusControllerTest : AbstractPageControllerTest() {
    @Test
    fun status() {
        navigateTo("/taxes/${tax.id}/status")

        assertCurrentPageIs(PageName.TAX_STATUS)

        select("#status", 3)
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateTaxStatusRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/taxes/${tax.id}/status"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals(TaxStatus.REVIEWING, request.firstValue.status)

        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun cancel() {
        navigateTo("/taxes/${tax.id}/status")

        assertCurrentPageIs(PageName.TAX_STATUS)

        select("#status", 3)
        click(".btn-cancel", 1000)

        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ACCOUNT_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            any<String>(),
            any<UpdateTaxStatusRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/taxes/${tax.id}/status")

        assertCurrentPageIs(PageName.TAX_STATUS)

        select("#status", 3)
        click("button[type=submit]", 1000)

        assertCurrentPageIs(PageName.TAX_STATUS)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/taxes/${tax.id}/edit")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `status - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes/${tax.id}/status")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

package com.wutsi.koki.portal.client.tax.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.client.AbstractPageControllerTest
import com.wutsi.koki.portal.client.AccountFixtures.account
import com.wutsi.koki.portal.client.TaxFixtures.tax
import com.wutsi.koki.portal.client.common.page.PageName
import com.wutsi.koki.tax.dto.GetTaxResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class TaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/taxes/${tax.id}")

        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `no access to module`() {
        disableModule("tax")

        navigateTo("/taxes/${tax.id}")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `not owner`() {
        doReturn(
            ResponseEntity(
                GetTaxResponse(tax.copy(accountId = 999L)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        doReturn(
            ResponseEntity(
                GetAccountResponse(account.copy(id = 999L)),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                eq("$sdkBaseUrl/v1/accounts/999"),
                eq(GetAccountResponse::class.java)
            )

        navigateTo("/taxes/${tax.id}")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `not found`() {
        doThrow(
            createHttpClientErrorException(404, ErrorCode.TAX_NOT_FOUND)
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(GetTaxResponse::class.java)
            )

        navigateTo("/taxes/${tax.id}")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun `invalid id`() {
        navigateTo("/taxes/four-hundred")

        assertCurrentPageIs(PageName.ERROR_404)
    }

    @Test
    fun anonymous() {
        setUpAnonymousUser()

        navigateTo("/taxes/${tax.id}")

        assertCurrentPageIs(PageName.LOGIN)
    }
}

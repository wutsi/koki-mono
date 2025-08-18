package com.wutsi.koki.portal.forgot.page

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ResetPasswordRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class ResetPasswordTest : AbstractPageControllerTest() {
    @Test
    fun forgot() {
        navigateTo("/forgot/password/reset?token=12345")

        assertCurrentPageIs(PageName.FORGOT_PASSWORD_RESET)
        assertElementNotPresent(".alert-danger")
        input("#password", "Secret123")
        input("#confirm-password", "Secret123")
        click("button[type=submit]")

        val request = argumentCaptor<ResetPasswordRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/password/reset"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals("12345", request.firstValue.tokenId)
        assertEquals("Secret123", request.firstValue.password)

        assertCurrentPageIs(PageName.FORGOT_PASSWORD_RESET_DONE)
        click("#btn-next")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(404, ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/password/reset"),
            any(),
            eq(Any::class.java),
        )

        navigateTo("/forgot/password/reset?token=12345")
        input("#password", "Secret123")
        input("#confirm-password", "Secret123")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.FORGOT_PASSWORD_RESET)
        assertElementPresent(".alert-danger")
    }
}

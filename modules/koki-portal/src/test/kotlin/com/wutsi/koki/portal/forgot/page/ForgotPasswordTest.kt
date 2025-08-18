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
import com.wutsi.koki.tenant.dto.SendPasswordRequest
import com.wutsi.koki.tenant.dto.SendPasswordResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class ForgotPasswordTest : AbstractPageControllerTest() {
    @Test
    fun forgot() {
        navigateTo("/forgot/password")

        assertCurrentPageIs(PageName.FORGOT_PASSWORD)
        assertElementNotPresent(".alert-danger")
        input("#email", "ray.sponsible@gmail.com")
        click("button[type=submit]")

        val request = argumentCaptor<SendPasswordRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/password/send"),
            request.capture(),
            eq(SendPasswordResponse::class.java),
        )
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)

        assertCurrentPageIs(PageName.FORGOT_PASSWORD_DONE)
        click("#btn-next")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `bad email`() {
        val ex = createHttpClientErrorException(404, ErrorCode.USER_NOT_FOUND)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/password/send"),
            any(),
            eq(SendPasswordResponse::class.java),
        )

        navigateTo("/forgot/password")
        input("#email", "ray.sponsible@gmail.com")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.FORGOT_PASSWORD)
        assertElementPresent(".alert-danger")
    }
}

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
import com.wutsi.koki.tenant.dto.SendUsernameRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class ForgotUsernameTest : AbstractPageControllerTest() {
    @Test
    fun forgot() {
        navigateTo("/forgot/username")

        assertCurrentPageIs(PageName.FORGOT_USERNAME)
        assertElementNotPresent(".alert-danger")
        input("#email", "ray.sponsible@gmail.com")
        click("button[type=submit]")

        val request = argumentCaptor<SendUsernameRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/username/send"),
            request.capture(),
            eq(Any::class.java),
        )
        assertEquals("ray.sponsible@gmail.com", request.firstValue.email)

        assertCurrentPageIs(PageName.FORGOT_USERNAME_DONE)
        click("#btn-next")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `bad email`() {
        val ex = createHttpClientErrorException(404, ErrorCode.USER_NOT_FOUND)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/username/send"),
            any(),
            eq(Any::class.java),
        )

        navigateTo("/forgot/username")
        input("#email", "ray.sponsible@gmail.com")
        click("button[type=submit]")

        assertCurrentPageIs(PageName.FORGOT_USERNAME)
        assertElementPresent(".alert-danger")
    }
}

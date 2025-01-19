package com.wutsi.koki.portal.user.page.settings.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import com.wutsi.koki.tenant.dto.UserStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditUserControllerTest : AbstractPageControllerTest() {
    @Test
    fun update() {
        navigateTo("/settings/users/${user.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_EDIT)

        input("#displayName", "Yo Man")
        input("#email", "yoman@gmail.com")
        select("#status", 2)
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals("Yo Man", request.firstValue.displayName)
        assertEquals("yoman@gmail.com", request.firstValue.email)
        assertEquals(UserStatus.SUSPENDED, request.firstValue.status)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER)
        assertElementVisible("#user-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}"),
            any<UpdateUserRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/settings/users/${user.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_EDIT)

        input("#displayName", "Yo Man")
        input("#email", "yoman@gmail.com")
        select("#status", 2)
        click("button[type=submit]", 1000)

        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_EDIT)
    }

    @Test
    fun back() {
        navigateTo("/settings/users/${user.id}/edit")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
    }
}

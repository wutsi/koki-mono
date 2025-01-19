package com.wutsi.koki.portal.role.page.settings.role

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import com.wutsi.koki.tenant.dto.SetRoleListRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SettingsEditUserRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun update() {
        navigateTo("/settings/users/${user.id}/roles")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_ROLE)

        scrollToBottom()
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SetRoleListRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}/roles"),
            request.capture(),
            eq(Any::class.java),
        )

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER)
        assertElementVisible("#user-toast")
    }

    @Test
    fun noRole() {
        doReturn(
            ResponseEntity(
                SearchRoleResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchRoleResponse::class.java)
            )

        navigateTo("/settings/users/${user.id}/roles")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_ROLE)

        assertElementNotPresent("button[type=submit]")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.ROLE_IN_USE)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}/roles"),
            any<SetRoleListRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/settings/users/${user.id}/roles")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_ROLE)

        scrollToBottom()
        click("button[type=submit]", 1000)

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_ROLE)
    }

    @Test
    fun back() {
        navigateTo("/settings/users/${user.id}/roles")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
    }
}

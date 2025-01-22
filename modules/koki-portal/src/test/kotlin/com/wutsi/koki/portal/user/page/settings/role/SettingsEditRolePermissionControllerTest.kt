package com.wutsi.koki.portal.user.page.settings.role

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.RoleFixtures.role
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.SetPermissionListRequest
import kotlin.test.Test

class SettingsEditRolePermissionControllerTest : AbstractPageControllerTest() {
    @Test
    fun update() {
        navigateTo("/settings/roles/${role.id}/permissions")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_PERMISSION)

        scrollToBottom()
        click("button[type=submit]", 1000)

        val request = argumentCaptor<SetPermissionListRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/roles/${role.id}/permissions"),
            request.capture(),
            eq(Any::class.java),
        )

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/roles/${role.id}/permissions"),
            any<SetPermissionListRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/settings/roles/${role.id}/permissions")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_PERMISSION)

        scrollToBottom()
        click("button[type=submit]", 1000)

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_PERMISSION)
    }

    @Test
    fun back() {
        navigateTo("/settings/roles/${role.id}/permissions")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }
}

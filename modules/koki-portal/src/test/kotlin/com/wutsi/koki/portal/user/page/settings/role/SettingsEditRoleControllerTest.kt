package com.wutsi.koki.portal.user.page.settings.role

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.RoleFixtures
import com.wutsi.koki.RoleFixtures.role
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.UpdateRoleRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/roles/${role.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        scrollToBottom()
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateRoleRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/roles/${role.id}"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals("ACCT", request.firstValue.name)
        assertEquals("Accountant", request.firstValue.title)
        assertEquals("This is an accountant that fill taxes", request.firstValue.description)
        assertEquals(false, request.firstValue.active)
        assertEquals(
            RoleFixtures.role.permissionIds.sorted(),
            request.firstValue.permissionIds.sorted()
        )
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/roles/${role.id}"),
            any<UpdateRoleRequest>(),
            eq(Any::class.java),
        )

        navigateTo("/settings/roles/${role.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        scrollToBottom()
        click("button[type=submit]", 1000)

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun back() {
        navigateTo("/settings/roles/${role.id}/edit")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }

    @Test
    fun `edit - without permission security-admin`() {
        setUpUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/roles/${role.id}/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

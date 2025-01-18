package com.wutsi.koki.portal.user.page.settings

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
import com.wutsi.koki.tenant.dto.UpdateRoleRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun update() {
        navigateTo("/settings/security/roles/${role.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateRoleRequest>()
        verify(kokiUsers).updateRole(eq(role.id), request.capture())
        assertEquals("ACCT", request.firstValue.name)
        assertEquals("Accountant", request.firstValue.title)
        assertEquals("This is an accountant that fill taxes", request.firstValue.description)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
        assertElementVisible("#role-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(kokiUsers).updateRole(any(), any())

        navigateTo("/settings/security/roles/${role.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        click("button[type=submit]", 1000)
        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun back() {
        navigateTo("/settings/security/roles/${role.id}/edit")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }
}

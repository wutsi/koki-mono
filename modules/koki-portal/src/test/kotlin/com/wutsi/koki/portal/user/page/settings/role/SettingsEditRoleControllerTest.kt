package com.wutsi.koki.portal.user.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsCreateRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/settings/security/roles/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateRoleRequest>()
        verify(kokiUsers).createRole(request.capture())
        assertEquals("ACCT", request.firstValue.name)
        assertEquals("Accountant", request.firstValue.title)
        assertEquals("This is an accountant that fill taxes", request.firstValue.description)
        assertEquals(false, request.firstValue.active)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
        assertElementVisible("#role-toast-created")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(kokiUsers).createRole(any())

        navigateTo("/settings/security/roles/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        click("button[type=submit]", 1000)
        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)
    }
}

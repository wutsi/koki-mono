package com.wutsi.koki.portal.user.page.settings.role

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ModuleFixtures
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.CreateRoleRequest
import com.wutsi.koki.tenant.dto.CreateRoleResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsCreateRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/settings/roles/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        click("#permission-" + ModuleFixtures.permissions[0].id)
        click("#permission-" + ModuleFixtures.permissions[2].id)
        click("#permission-" + ModuleFixtures.permissions[5].id)
        scrollToBottom()
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateRoleRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/roles"),
            request.capture(),
            eq(CreateRoleResponse::class.java),
        )

        assertEquals("ACCT", request.firstValue.name)
        assertEquals("Accountant", request.firstValue.title)
        assertEquals("This is an accountant that fill taxes", request.firstValue.description)
        assertEquals(false, request.firstValue.active)
        assertEquals(
            listOf(
                ModuleFixtures.permissions[0].id,
                ModuleFixtures.permissions[2].id,
                ModuleFixtures.permissions[5].id
            ),
            request.firstValue.permissionIds
        )
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/roles"),
            any(),
            eq(CreateRoleResponse::class.java),
        )

        navigateTo("/settings/roles/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)

        input("#name", "ACCT")
        input("#title", "Accountant")
        input("#description", "This is an accountant that fill taxes")
        select("#active", 1)
        scrollToBottom()
        click("button[type=submit]", 1000)

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_CREATE)
    }

    @Test
    fun back() {
        navigateTo("/settings/roles/create")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }

    @Test
    fun `create - without permission security-admin`() {
        setupUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/roles/create")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

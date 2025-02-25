package com.wutsi.koki.portal.user.page.settings.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.RoleFixtures
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.CreateUserResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsCreateUserControllerTest : AbstractPageControllerTest() {
    @Test
    fun create() {
        navigateTo("/settings/users/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_CREATE)

        input("#displayName", "Yo Man")
        input("#email", "yoman@gmail.com")
        input("#password", "secret")
        click("#role-" + RoleFixtures.roles[0].id)
        click("#role-" + RoleFixtures.roles[2].id)
        input("#password", "secret")
        click("button[type=submit]", 1000)

        val request = argumentCaptor<CreateUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users"),
            request.capture(),
            eq(CreateUserResponse::class.java),
        )

        assertEquals("Yo Man", request.firstValue.displayName)
        assertEquals("yoman@gmail.com", request.firstValue.email)
        assertEquals("secret", request.firstValue.password)
        assertEquals(listOf(RoleFixtures.roles[0].id, RoleFixtures.roles[2].id), request.firstValue.roleIds)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users"),
            any(),
            eq(CreateUserResponse::class.java),
        )

        navigateTo("/settings/users/create")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_CREATE)

        input("#displayName", "Yo Man")
        input("#email", "yoman@gmail.com")
        input("#password", "secret")
        click("button[type=submit]", 1000)

        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_CREATE)
    }

    @Test
    fun back() {
        navigateTo("/settings/users/create")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
    }

    @Test
    fun `create - without permission security-admin`() {
        setUpUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/users/create")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

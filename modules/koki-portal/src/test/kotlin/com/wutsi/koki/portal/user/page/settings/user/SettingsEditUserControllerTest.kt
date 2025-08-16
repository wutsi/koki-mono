package com.wutsi.koki.portal.user.page.settings.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.RoleFixtures
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.UpdateUserRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsEditUserControllerTest : AbstractPageControllerTest() {
    @Test
    fun edit() {
        navigateTo("/settings/users/${user.id}/edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_EDIT)

        input("#displayName", "Yo Man")
        input("#email", "yoman@gmail.com")
        select2("#language", "French")
        click("#role-" + RoleFixtures.roles[0].id)
        click("#role-" + RoleFixtures.roles[2].id)
        click("button[type=submit]", 1000)

        val request = argumentCaptor<UpdateUserRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/users/${user.id}"),
            request.capture(),
            eq(Any::class.java),
        )

        assertEquals("Yo Man", request.firstValue.displayName)
        assertEquals("yoman@gmail.com", request.firstValue.email)
        assertEquals("fr", request.firstValue.language)
        assertEquals(
            RoleFixtures.roles.filter { role ->
                role.id != RoleFixtures.roles[0].id && role.id != RoleFixtures.roles[2].id
            }.map { role -> role.id },
            request.firstValue.roleIds
        )

        assertEquals(user.cityId, request.firstValue.cityId)
        assertEquals(user.country, request.firstValue.country)
        assertEquals(user.employer, request.firstValue.employer)
        assertEquals(user.mobile, request.firstValue.mobile)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER)
        assertElementVisible("#koki-toast")
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

    @Test
    fun `edit - without permission security-admin`() {
        setupUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/users/${user.id}/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

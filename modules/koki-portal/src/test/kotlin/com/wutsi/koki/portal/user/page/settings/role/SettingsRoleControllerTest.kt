package com.wutsi.koki.portal.user.page.settings.role

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.RoleFixtures.role
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/roles/${role.id}")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }

    @Test
    fun back() {
        navigateTo("/settings/roles/${role.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }

    @Test
    fun edit() {
        navigateTo("/settings/roles/${role.id}")
        click("#btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun portalSignupRole() {
        navigateTo("/settings/roles/${role.id}")
        click("#btn-portal-signup-role")

        val request = argumentCaptor<SaveConfigurationRequest>()
        verify(rest).postForEntity(
            eq("$sdkBaseUrl/v1/configurations"),
            request.capture(),
            eq(Any::class.java)
        )
        assertEquals(role.id.toString(), request.firstValue.values[ConfigurationName.PORTAL_SIGNUP_ROLE_ID])

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }

    @Test
    fun `show - without permission security-admin`() {
        setupUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/roles/${role.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

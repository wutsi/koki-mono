package com.wutsi.koki.portal.user.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.RoleFixtures.role
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsRoleControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/security/roles/${role.id}")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }

    @Test
    fun back() {
        navigateTo("/settings/security/roles/${role.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }

    @Test
    fun edit() {
        navigateTo("/settings/security/roles/${role.id}")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)

        click(".btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun permission() {
        navigateTo("/settings/security/roles/${role.id}")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)

        click(".btn-permission")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_PERMISSION)
    }

    @Test
    fun delete() {
        navigateTo("/settings/security/roles/${role.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(kokiUsers).deleteRole(role.id)

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
        assertElementVisible("#role-toast")
    }

    @Test
    fun `delete failed`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(kokiUsers).deleteRole(any())

        navigateTo("/settings/security/roles/${role.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }
}

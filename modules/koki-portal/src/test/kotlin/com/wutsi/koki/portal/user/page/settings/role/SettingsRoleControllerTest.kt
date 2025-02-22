package com.wutsi.koki.portal.user.page.settings.role

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
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)

        click(".btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun delete() {
        navigateTo("/settings/roles/${role.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        verify(rest).delete("$sdkBaseUrl/v1/roles/${role.id}")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
        assertElementVisible("#koki-toast")
    }

    @Test
    fun `delete failed`() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(rest).delete(any<String>())

        navigateTo("/settings/roles/${role.id}")
        click(".btn-delete")

        val alert = driver.switchTo().alert()
        alert.accept()
        driver.switchTo().parentFrame()

        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE)
    }

    @Test
    fun `show - without permission security-admin`() {
        setUpUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/roles/${role.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

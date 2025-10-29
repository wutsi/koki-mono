package com.wutsi.koki.portal.user.page.settings.role

import com.wutsi.koki.RoleFixtures.role
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
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
        click("#btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_EDIT)
    }

    @Test
    fun `show - without permission security-admin`() {
        setupUserWithoutPermissions(listOf("security:admin"))

        navigateTo("/settings/roles/${role.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

package com.wutsi.koki.portal.user.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsSecurityControllerTest : AbstractPageControllerTest() {
    @Test
    fun security() {
        navigateTo("/settings/security")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS)
    }

    @Test
    fun roles() {
        navigateTo("/settings/security")
        click(".btn-role")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_ROLE_LIST)
    }

    @Test
    fun users() {
        navigateTo("/settings/security")
        click(".btn-user")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
    }
}

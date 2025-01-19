package com.wutsi.koki.portal.user.page.settings.user

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.UserFixtures.user
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsUserControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/users/${user.id}")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER)
    }

    @Test
    fun back() {
        navigateTo("/settings/users/${user.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_LIST)
    }

    @Test
    fun edit() {
        navigateTo("/settings/users/${user.id}")
        click(".btn-edit")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_EDIT)
    }

    @Test
    fun role() {
        navigateTo("/settings/users/${user.id}")
        click(".btn-role")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS_USER_ROLE)
    }
}

package com.wutsi.koki.portal.account.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsAccountControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/accounts")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS)
    }

    @Test
    fun attributes() {
        navigateTo("/settings/accounts")
        click(".btn-attribute")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_LIST)
    }

    @Test
    fun `without permission account-admin`() {
        setUpUserWithoutPermissions(listOf("account:admin"))

        navigateTo("/settings/accounts")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

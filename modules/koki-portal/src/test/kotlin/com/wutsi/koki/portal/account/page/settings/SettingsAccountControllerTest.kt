package com.wutsi.koki.portal.account.page.settings

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
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
        setupUserWithoutPermissions(listOf("account:admin"))

        navigateTo("/settings/accounts")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

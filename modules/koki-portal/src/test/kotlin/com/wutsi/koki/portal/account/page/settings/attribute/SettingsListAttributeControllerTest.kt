package com.wutsi.koki.portal.account.page.settings.type

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsListAttributeControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/accounts/attributes")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_LIST)
    }

    @Test
    fun import() {
        navigateTo("/settings/accounts/attributes")
        click(".btn-import")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/accounts/attributes")
        click(".btn-back")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS)
    }

    @Test
    fun `without permission account-admin`() {
        setUpUserWithoutPermissions(listOf("account:admin"))

        navigateTo("/settings/accounts/attributes")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

package com.wutsi.koki.portal.account.page.settings.type

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
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
        setupUserWithoutPermissions(listOf("account:admin"))

        navigateTo("/settings/accounts/attributes")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

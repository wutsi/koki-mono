package com.wutsi.koki.portal.account.page.settings.attribute

import com.wutsi.koki.AccountFixtures.attribute
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsAttributeControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/accounts/attributes/${attribute.id}")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE)
    }

    @Test
    fun back() {
        navigateTo("/settings/accounts/attributes/${attribute.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_ATTRIBUTE_LIST)
    }

    @Test
    fun `without permission account-admin`() {
        setUpUserWithoutPermissions(listOf("account:admin"))

        navigateTo("/settings/accounts/attributes/${attribute.id}")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

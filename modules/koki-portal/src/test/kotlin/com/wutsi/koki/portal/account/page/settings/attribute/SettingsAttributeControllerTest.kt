package com.wutsi.koki.portal.account.page.settings.attribute

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.attribute
import com.wutsi.koki.portal.page.PageName
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
}

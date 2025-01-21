package com.wutsi.koki.portal.account.page.settings.type

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.AccountFixtures.accountType
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsAccountTypeControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/accounts/types/${accountType.id}")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_TYPE)
    }

    @Test
    fun back() {
        navigateTo("/settings/accounts/types/${accountType.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.ACCOUNT_SETTINGS_TYPE_LIST)
    }
}

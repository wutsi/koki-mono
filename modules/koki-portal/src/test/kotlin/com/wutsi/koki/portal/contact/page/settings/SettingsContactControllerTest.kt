package com.wutsi.koki.portal.tax.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsContactControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/contacts")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS)
    }

    @Test
    fun types() {
        navigateTo("/settings/contacts")
        click(".btn-type")
        assertCurrentPageIs(PageName.CONTACT_SETTINGS_TYPE_LIST)
    }
}

package com.wutsi.koki.portal.tax.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/taxes")
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }

    @Test
    fun types() {
        navigateTo("/settings/taxes")
        click(".btn-type")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_LIST)
    }
}

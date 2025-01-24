package com.wutsi.koki.portal.tax.page.settings.type

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsListTaxTypeControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/settings/taxes/types")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_LIST)
    }

    @Test
    fun import() {
        navigateTo("/settings/taxes/types")
        click(".btn-import")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_IMPORT)
    }

    @Test
    fun back() {
        navigateTo("/settings/taxes/types")
        click(".btn-back")
        assertCurrentPageIs(PageName.TAX_SETTINGS)
    }
}

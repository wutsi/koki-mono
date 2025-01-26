package com.wutsi.koki.portal.tax.page.settings.type

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.tax
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsTaxControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/taxes/types/${tax.id}")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE)
    }

    @Test
    fun back() {
        navigateTo("/settings/taxes/types/${tax.id}")
        click(".btn-back")
        assertCurrentPageIs(PageName.TAX_SETTINGS_TYPE_LIST)
    }

    @Test
    fun `show - without permission tax-admin`() {
        setUpUserWithoutPermissions(listOf("tax:admin"))

        navigateTo("/settings/taxes/types/${tax.id}")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

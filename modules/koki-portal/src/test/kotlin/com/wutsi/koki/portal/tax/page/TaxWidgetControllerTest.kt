package com.wutsi.koki.portal.tax.page

import com.wutsi.koki.TaxFixtures.taxes
import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class TaxWidgetControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/taxes/widget")

        assertElementCount(".widget-taxes tr.tax", taxes.size)
    }

    @Test
    fun `list - without permission tax`() {
        setUpUserWithoutPermissions(listOf("tax"))

        navigateTo("/taxes/widget")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

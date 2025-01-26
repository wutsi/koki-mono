package com.wutsi.koki.portal.tax.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.taxes
import com.wutsi.koki.portal.page.PageName
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
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

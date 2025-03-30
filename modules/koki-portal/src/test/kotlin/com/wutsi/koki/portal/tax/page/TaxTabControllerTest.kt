package com.wutsi.koki.portal.tax.page

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.TaxFixtures.taxes
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class TaxTabControllerTest : AbstractPageControllerTest() {
    @Test
    fun list() {
        navigateTo("/taxes/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")

        assertElementCount(".tab-taxes tr.tax", taxes.size)
    }

    @Test
    fun addNew() {
        navigateTo("/taxes/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        click(".btn-add-tax")

        assertCurrentPageIs(PageName.TAX_CREATE)
    }

    @Test
    fun view() {
        navigateTo("/taxes/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        click("tr.tax a")

        assertCurrentPageIs(PageName.TAX)
    }

    @Test
    fun `list - without permission tax-manage`() {
        setUpUserWithoutPermissions(listOf("tax:manage"))

        navigateTo("/taxes/tab?owner-id=111&owner-type=ACCOUNT&test-mode=true")

        assertElementNotPresent(".btn-add-tax")
        assertElementNotPresent(".btn-edit")
    }

    @Test
    fun `list - without permission tax`() {
        setUpUserWithoutPermissions(listOf("tax"))

        navigateTo("/taxes/tab?test-mode=true&owner-id=111&owner-type=ACCOUNT")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

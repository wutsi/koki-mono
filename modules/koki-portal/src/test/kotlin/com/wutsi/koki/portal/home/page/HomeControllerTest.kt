package com.wutsi.koki.portal.home.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class HomeControllerTest : AbstractPageControllerTest() {
    @Test
    fun home() {
        navigateTo("/")
        assertCurrentPageIs(PageName.HOME)

        assertElementPresent("#btn-create-listing")
        assertElementPresent("#btn-create-contact")

        Thread.sleep(1000)
        assertElementPresent("#widget-leads-new")
        assertElementPresent("#widget-offers-active")
        assertElementPresent("#widget-listings-recent")
        assertElementPresent("#widget-listings-sold")
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/")
        assertCurrentPageIs(PageName.LOGIN)
    }
}

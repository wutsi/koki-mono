package com.wutsi.koki.portal.home.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class HomeControllerTest : AbstractPageControllerTest() {
    @Test
    fun home() {
        navigateTo("/")
        assertCurrentPageIs(PageName.HOME)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `message widget`() {
        navigateTo("/")

        Thread.sleep(1000)
        assertElementPresent(".widget-messages")

        click(".widget-messages tr.message a")
        assertElementVisible("#koki-modal")
    }
}

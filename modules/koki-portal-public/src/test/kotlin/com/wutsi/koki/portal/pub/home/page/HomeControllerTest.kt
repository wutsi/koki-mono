package com.wutsi.koki.portal.pub.home.page

import com.wutsi.koki.portal.pub.AbstractPageControllerTest
import com.wutsi.koki.portal.pub.common.page.PageName
import kotlin.test.Test

class HomeControllerTest : AbstractPageControllerTest() {
    @Test
    fun home() {
        navigateTo("/")
        assertCurrentPageIs(PageName.HOME)

        assertElementAttribute("html", "lang", "fr")
    }

    @Test
    fun `home - english translation`() {
        navigateTo("/?lang=en")
        assertCurrentPageIs(PageName.HOME)

        assertElementAttribute("html", "lang", "en")
    }
}

package com.wutsi.koki.room.web.home.page

import com.wutsi.koki.room.web.AbstractPageControllerTest
import com.wutsi.koki.room.web.common.page.PageName
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

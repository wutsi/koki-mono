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
}

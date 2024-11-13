package com.wutsi.koki.portal.page.form

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
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
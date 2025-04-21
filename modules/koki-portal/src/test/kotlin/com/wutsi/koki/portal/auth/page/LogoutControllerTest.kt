package com.wutsi.koki.portal.auth.page

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class LogoutControllerTest : AbstractPageControllerTest() {
    @Test
    fun home() {
        navigateTo("/")
        navigateTo("/logout")
        assertCurrentPageIs(PageName.LOGIN)

        verify(accessTokenHolder).remove()
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/")
        assertCurrentPageIs(PageName.LOGIN)
    }
}

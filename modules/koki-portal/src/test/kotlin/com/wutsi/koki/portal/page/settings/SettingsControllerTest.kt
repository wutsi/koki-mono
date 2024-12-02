package com.wutsi.koki.portal.page.settings

import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import kotlin.test.Test

class SettingsControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings")
        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun `login required`() {
        setUpAnonymousUser()

        navigateTo("/settings")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun messages() {
        navigateTo("/settings")
        click(".btn-message")
        assertCurrentPageIs(PageName.MESSAGE_LIST)
    }

    @Test
    fun workflows() {
        navigateTo("/settings")
        click(".btn-workflow")
        assertCurrentPageIs(PageName.WORKFLOW_LIST)
    }
}

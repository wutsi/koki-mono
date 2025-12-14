package com.wutsi.koki.portal.settings.page

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
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
    fun email() {
        navigateTo("/settings")
        click(".btn-email")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS)
    }

    @Test
    fun security() {
        navigateTo("/settings")
        click(".btn-security")
        assertCurrentPageIs(PageName.SECURITY_SETTINGS)
    }
}

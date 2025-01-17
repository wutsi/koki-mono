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
    fun forms() {
        navigateTo("/settings")
        click(".btn-form")
        assertCurrentPageIs(PageName.SETTINGS_FORM_LIST)
    }

    @Test
    fun messages() {
        navigateTo("/settings")
        click(".btn-message")
        assertCurrentPageIs(PageName.SETTINGS_MESSAGE_LIST)
    }

    @Test
    fun workflows() {
        navigateTo("/settings")
        click(".btn-workflow")
        assertCurrentPageIs(PageName.SETTINGS_WORKFLOW_LIST)
    }

    @Test
    fun email() {
        navigateTo("/settings")
        click(".btn-email")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS)
    }

    @Test
    fun scripts() {
        navigateTo("/settings")
        click(".btn-script")
        assertCurrentPageIs(PageName.SETTINGS_SCRIPT_LIST)
    }

    @Test
    fun services() {
        navigateTo("/settings")
        click(".btn-service")
        assertCurrentPageIs(PageName.SETTINGS_SERVICE_LIST)
    }
}

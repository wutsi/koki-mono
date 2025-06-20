package com.wutsi.koki.portal.email.page.settings

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsEmailControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/email")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS)
    }

    @Test
    fun smtp() {
        navigateTo("/settings/email")
        click(".btn-smtp")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP)
    }

    @Test
    fun decorator() {
        navigateTo("/settings/email")
        click(".btn-decorator")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_EMAIL_DECORATOR)
    }

    @Test
    fun `without permission email-admin`() {
        setupUserWithoutPermissions(listOf("email:admin"))

        navigateTo("/settings/email")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

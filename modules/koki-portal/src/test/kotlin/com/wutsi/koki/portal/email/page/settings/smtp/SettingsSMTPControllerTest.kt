package com.wutsi.koki.portal.email.page.settings.smtp

import com.wutsi.koki.portal.AbstractPageControllerTest
import com.wutsi.koki.portal.common.page.PageName
import kotlin.test.Test

class SettingsSMTPControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/email/smtp")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP)

        assertElementPresent(".table")
    }

    @Test
    fun back() {
        navigateTo("/settings/email/smtp")

        click(".btn-back")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS)
    }

    @Test
    fun edit() {
        navigateTo("/settings/email/smtp")

        click(".btn-edit")

        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)
    }

    @Test
    fun `without permission email-admin`() {
        setupUserWithoutPermissions(listOf("email:admin"))

        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.ERROR_403)
    }
}

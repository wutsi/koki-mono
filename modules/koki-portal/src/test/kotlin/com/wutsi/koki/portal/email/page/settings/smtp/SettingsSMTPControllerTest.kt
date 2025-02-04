package com.wutsi.koki.portal.email.page.settings.smtp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class SettingsSMTPControllerTest : AbstractPageControllerTest() {
    @Test
    fun show() {
        navigateTo("/settings/email/smtp")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP)

        assertElementPresent(".table")
        assertElementNotPresent(".empty")
    }

    @Test
    fun back() {
        navigateTo("/settings/email/smtp")

        click(".btn-back")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS)
    }

    @Test
    fun `not configured`() {
        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )

        navigateTo("/settings/email/smtp")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP)

        assertElementNotPresent(".table")
        assertElementPresent(".empty")
    }

    @Test
    fun edit() {
        navigateTo("/settings/email/smtp")

        click(".btn-edit")

        assertCurrentPageIs(PageName.EMAIL_SETTINGS_SMTP_EDIT)
    }

    @Test
    fun `without permission email-admin`() {
        setUpUserWithoutPermissions(listOf("email:admin"))

        navigateTo("/settings/email/smtp/edit")
        assertCurrentPageIs(PageName.ERROR_ACCESS_DENIED)
    }
}

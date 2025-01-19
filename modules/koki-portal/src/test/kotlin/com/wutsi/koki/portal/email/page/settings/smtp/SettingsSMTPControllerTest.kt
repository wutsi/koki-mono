package com.wutsi.koki.portal.email.page.settings.smtp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.collections.map
import kotlin.test.Test
import kotlin.to

class SettingsSMTPControllerTest : AbstractPageControllerTest() {
    private val config = mapOf(
        ConfigurationName.SMTP_PORT to "25",
        ConfigurationName.SMTP_HOST to "smtp.gmail.com",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki",
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            ResponseEntity(
                SearchConfigurationResponse(
                    configurations = config.map { cfg -> Configuration(name = cfg.key, value = cfg.value) }
                ),
                HttpStatus.OK,
            )
        ).whenever(rest)
            .getForEntity(
                any<String>(),
                eq(SearchConfigurationResponse::class.java)
            )
    }

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
}

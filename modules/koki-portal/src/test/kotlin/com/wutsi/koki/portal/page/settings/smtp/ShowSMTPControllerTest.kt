package com.wutsi.koki.portal.page.settings.smtp

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ShowSMTPControllerTest : AbstractPageControllerTest() {
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
            SearchConfigurationResponse(
                config.map { cfg -> Configuration(name = cfg.key, value = cfg.value) }
            )
        ).whenever(kokiTenants).configurations(anyOrNull(), anyOrNull())
    }

    @Test
    fun show() {
        navigateTo("/settings/smtp")
        assertCurrentPageIs(PageName.SETTINGS_SMTP)

        assertElementPresent(".table")
        assertElementNotPresent(".empty")
    }

    @Test
    fun `not configured`() {
        doReturn(SearchConfigurationResponse()).whenever(kokiTenants).configurations(anyOrNull(), anyOrNull())

        navigateTo("/settings/smtp")
        assertCurrentPageIs(PageName.SETTINGS_SMTP)

        assertElementNotPresent(".table")
        assertElementPresent(".empty")
    }

    @Test
    fun edit() {
        navigateTo("/settings/smtp")

        click(".btn-edit")

        assertCurrentPageIs(PageName.SETTINGS_SMTP_EDIT)
    }
}

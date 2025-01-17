package com.wutsi.koki.portal.email.page.settings.decorator

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.AbstractPageControllerTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.portal.page.PageName
import com.wutsi.koki.tenant.dto.Configuration
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class SettingsEmailDecoratorEditControllerTest : AbstractPageControllerTest() {
    private val config = mapOf(
        ConfigurationName.EMAIL_DECORATOR to
            """
            <html>
            <head>
                <meta charset="utf-8">
            </head>
            <body>
            <table role="presentation"
                   style="max-width: 800px; width: 96%; border-spacing: 0; font-size: 18px; margin: 16px auto 0 auto; border: 1px solid gray;">
                <tr>
                    <td style="text-align: right; padding: 32px;">
                        {{#tenant_website_url}}
                        <a href="{{tenant_website_url}}" style="text-decoration: none;">
                            <img height="64" src="{{tenant_icon_url}}" style="height: 64px; max-width: 64px"/>
                        </a>
                        {{/tenant_website_url}}
                        {{^tenant_website_url}}
                        <img src="{{tenant_icon_url}}" style="height: 64px; max-width: 64px"/>
                        {{/tenant_website_url}}
                    </td>
                </tr>
                <tr>
                    <td style="padding: 16px">
                        {{{body}}}
                    </td>
                </tr>
            </table>
            </body>
            </html>
        """.trimIndent(),
    )

    private val code = "<html><body>{{{body}}}</body></html>"

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(
            SearchConfigurationResponse(
                config.map { cfg -> Configuration(name = cfg.key, value = cfg.value) }
            )
        ).whenever(kokiConfiguration).configurations(anyOrNull(), anyOrNull())
    }

    @Test
    fun edit() {
        navigateTo("/settings/email/decorator/edit")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_EMAIL_DECORATOR_EDIT)

        inputCodeMiror(code)
        click("button[type=submit]", 1000)

        verify(kokiConfiguration).save(
            SaveConfigurationRequest(
                values = mapOf(ConfigurationName.EMAIL_DECORATOR to code)
            )
        )

        assertCurrentPageIs(PageName.EMAIL_SETTINGS_EMAIL_DECORATOR_SAVED)
    }

    @Test
    fun error() {
        val ex = createHttpClientErrorException(statusCode = 409, errorCode = ErrorCode.AUTHORIZATION_PERMISSION_DENIED)
        doThrow(ex).whenever(kokiConfiguration).save(any())

        navigateTo("/settings/email/decorator/edit")
        assertCurrentPageIs(PageName.EMAIL_SETTINGS_EMAIL_DECORATOR_EDIT)

        inputCodeMiror(code)
        click("button[type=submit]", 1000)
        assertElementPresent(".alert-danger")

        assertCurrentPageIs(PageName.EMAIL_SETTINGS_EMAIL_DECORATOR_EDIT)
    }
}

package com.wutsi.koki.email.server.service.filter

import com.github.mustachejava.DefaultMustacheFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.templating.MustacheTemplatingEngine
import com.wutsi.koki.platform.templating.TemplatingEngine
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.TenantEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import com.wutsi.koki.tenant.server.service.TenantService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailDecoratorFilterTest {
    private val configurationService = mock<ConfigurationService>()
    private val tenantService = mock<TenantService>()
    private val templatingEngine = mock<TemplatingEngine>()
    private val filter = EmailDecoratorFilter(
        configurationService,
        tenantService,
        templatingEngine
    )

    private val config = ConfigurationEntity(
        value = """
            <p>{{body}}</p>
        """.trimIndent()
    )

    private val tenant = TenantEntity(
        id = 1L,
        name = "test",
        domainName = "test.koki.com",
        iconUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png",
        logoUrl = "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png",
        portalUrl = "https://test.koki.com",
        websiteUrl = "https://www.test.com",
    )

    @BeforeEach
    fun setUp() {
        doReturn(listOf(config)).whenever(configurationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        doReturn(tenant).whenever(tenantService).get(any())
    }

    @Test
    fun filter() {
        doReturn("YYY").whenever(templatingEngine).apply(any(), any())

        val result = filter.filter("XXX", 1L)

        assertEquals("YYY", result)

        val context = argumentCaptor<Map<String, Any>>()
        verify(templatingEngine).apply(eq(config.value), context.capture())
        assertEquals(tenant.name, context.firstValue.get("tenant_name"))
        assertEquals(tenant.portalUrl, context.firstValue.get("tenant_portal_url"))
        assertEquals(tenant.logoUrl, context.firstValue.get("tenant_logo_url"))
        assertEquals(tenant.websiteUrl, context.firstValue.get("tenant_website_url"))
        assertEquals(tenant.iconUrl, context.firstValue.get("tenant_icon_url"))
        assertEquals("XXX", context.firstValue.get("body"))
    }

    @Test
    fun `default template`() {
        doReturn(emptyList<ConfigurationEntity>()).whenever(configurationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val xfilter = EmailDecoratorFilter(
            configurationService,
            tenantService,
            MustacheTemplatingEngine(DefaultMustacheFactory())
        )
        val result = xfilter.filter("Hello world", 1L)

        assertEquals(
            """
                <html>
                <head>
                    <meta charset="utf-8">
                    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
                    <link href="https://fonts.googleapis.com/css2?family=PT+Sans&display=swap" rel="stylesheet"/>
                    <meta content="width=device-width,initial-scale=1" name="viewport">
                    <meta name="x-apple-disable-message-reformatting">
                </head>
                <body>
                <table role="presentation"
                       style="max-width: 800px; width: 96%; border-spacing: 0; font-size: 18px; margin: 16px auto 0 auto; border: 1px solid gray;">
                    <tr>
                        <td style="text-align: right; padding: 32px;">
                            <a href="https://www.test.com" style="text-decoration: none;">
                                <img height="64" src="https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png" style="height: 64px; max-width: 64px"/>
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 16px">
                            Hello world
                        </td>
                    </tr>
                </table>
                </body>
                </html>

            """.trimIndent(),
            result
        )
    }
}

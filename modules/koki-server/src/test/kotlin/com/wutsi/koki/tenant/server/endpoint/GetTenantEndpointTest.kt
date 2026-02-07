package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.core.image.ImageService
import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.dto.TenantStatus
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetTenantEndpoint.sql"])
class GetTenantEndpointTest : TenantAwareEndpointTest() {
    @MockitoBean
    private lateinit var imageService: ImageService

    override fun setUp() {
        super.setUp()
        ignoreTenantIdHeader = true
    }

    @Test
    fun get() {
        // GIVEN
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        doReturn("https://img.com/logo-tiny.png")
            .whenever(imageService).transform(
                eq("https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png"),
                anyOrNull(),
            )

        doReturn("https://img.com/icon-tiny.png")
            .whenever(imageService).transform(
                eq("https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png"),
                anyOrNull(),
            )

        // WHEN
        val result = rest.getForEntity("/v1/tenants/1", GetTenantResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, result.statusCode)

        val tenant = result.body!!.tenant
        assertEquals(1L, tenant.id)
        assertEquals("test", tenant.name)
        assertEquals("test.com", tenant.domainName)
        assertEquals("CAD", tenant.currency)
        assertEquals("CA$", tenant.currencySymbol)
        assertEquals("en_CA", tenant.locale)
        assertEquals("2020-01-22", fmt.format(tenant.createdAt))
        assertEquals("#,###,###.#0", tenant.numberFormat)
        assertEquals("CA$ #,###,###.#0", tenant.monetaryFormat)
        assertEquals("yyyy-MM-dd", tenant.dateFormat)
        assertEquals("HH:mm", tenant.timeFormat)
        assertEquals("yyyy-MM-dd HH:mm", tenant.dateTimeFormat)
        assertEquals(TenantStatus.ACTIVE, tenant.status)
        assertEquals(
            "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png",
            tenant.logoUrl
        )
        assertEquals(
            "https://img.com/logo-tiny.png",
            tenant.logoTinyUrl
        )
        assertEquals(
            "https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png",
            tenant.iconUrl
        )
        assertEquals(
            "https://img.com/icon-tiny.png",
            tenant.iconTinyUrl
        )
        assertEquals("https://test.com", tenant.portalUrl)
        assertEquals("https://client.tenant-1.com", tenant.clientPortalUrl)
        assertEquals("CA", tenant.country)
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/tenants/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.TENANT_NOT_FOUND, result.body?.error?.code)
    }
}

package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.koki.tenant.dto.TenantStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/GetTenantEndpoint.sql"])
class GetTenantEndpointTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun get() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result = rest.getForEntity("/v1/tenants/1", GetTenantResponse::class.java)

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
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/tenants/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.TENANT_NOT_FOUND, result.body?.error?.code)
    }
}

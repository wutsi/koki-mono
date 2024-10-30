package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.tenant.dto.GetTenantResponse
import com.wutsi.platform.core.error.ErrorResponse
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
        assertEquals("USD", tenant.currency)
        assertEquals("en_US", tenant.locale)
        assertEquals(11L, tenant.ownerUserId)
        assertEquals("2020-01-22", fmt.format(tenant.createdAt))
    }

    @Test
    fun notFound() {
        val result = rest.getForEntity("/v1/tenants/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.TENANT_NOT_FOUND, result.body?.error?.code)
    }
}

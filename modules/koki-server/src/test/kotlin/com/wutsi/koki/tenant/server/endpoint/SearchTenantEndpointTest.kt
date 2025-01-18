package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchTenantResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchTenantEndpoint.sql"])
class SearchTenantEndpointTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Test
    fun all() {
        val result = rest.getForEntity("/v1/tenants", SearchTenantResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val tenants = result.body!!.tenants
        assertEquals(5, tenants.size)

        assertEquals(3, tenants[0].moduleIds.size)
    }
}

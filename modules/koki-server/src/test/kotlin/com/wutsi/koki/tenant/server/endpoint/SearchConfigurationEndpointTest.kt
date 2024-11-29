package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.SearchConfigurationResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchConfigurationEndpoint.sql"])
class SearchConfigurationEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/configurations", SearchConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val configurations = result.body!!.configurations
        assertEquals(4, configurations.size)

        assertEquals("a1", configurations[0].value)
        assertEquals("a", configurations[0].name)

        assertEquals("b1", configurations[1].value)
        assertEquals("b", configurations[1].name)

        assertEquals("c1", configurations[2].value)
        assertEquals("c", configurations[2].name)

        assertEquals("cd1", configurations[3].value)
        assertEquals("c.d", configurations[3].name)
    }

    @Test
    fun `by names`() {
        val result =
            rest.getForEntity("/v1/configurations?name=a&name=b", SearchConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val configurations = result.body!!.configurations
        assertEquals(2, configurations.size)

        assertEquals("a1", configurations[0].value)
        assertEquals("a", configurations[0].name)

        assertEquals("b1", configurations[1].value)
        assertEquals("b", configurations[1].name)
    }

    @Test
    fun `by keyword`() {
        val result =
            rest.getForEntity("/v1/configurations?q=c", SearchConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val configurations = result.body!!.configurations
        assertEquals(2, configurations.size)

        assertEquals("c1", configurations[0].value)
        assertEquals("c", configurations[0].name)

        assertEquals("cd1", configurations[1].value)
        assertEquals("c.d", configurations[1].name)
    }

    @Test
    fun `search configuration from another tenant`() {
        val result =
            rest.getForEntity("/v1/configurations?name=aa", SearchConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.configurations
        assertEquals(0, attributes.size)
    }
}

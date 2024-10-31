package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.SearchAttributeResponse
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
        assertEquals(3, configurations.size)

        assertEquals("a1", configurations[0].value)
        assertEquals("a", configurations[0].attribute.name)

        assertEquals("b1", configurations[1].value)
        assertEquals("b", configurations[1].attribute.name)

        assertEquals("c1", configurations[2].value)
        assertEquals("c", configurations[2].attribute.name)
    }

    @Test
    fun filter() {
        val result =
            rest.getForEntity("/v1/configurations?name=a&name=b", SearchConfigurationResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val configurations = result.body!!.configurations
        assertEquals(2, configurations.size)

        assertEquals("a1", configurations[0].value)
        assertEquals("a", configurations[0].attribute.name)

        assertEquals("b1", configurations[1].value)
        assertEquals("b", configurations[1].attribute.name)
    }

    @Test
    fun `search configuration from another tenant`() {
        val result =
            rest.getForEntity("/v1/configurations?name=aa", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(0, attributes.size)
    }
}

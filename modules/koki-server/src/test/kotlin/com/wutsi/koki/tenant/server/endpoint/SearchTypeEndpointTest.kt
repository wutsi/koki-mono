package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.SearchTypeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchTypeEndpoint.sql"])
class SearchTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/types", SearchTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val types = result.body!!.types
        assertEquals(4, types.size)
    }

    @Test
    fun `by keyowrd`() {
        val result = rest.getForEntity("/v1/types?q=tie", SearchTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val types = result.body!!.types
        assertEquals(2, types.size)
        assertEquals(100L, types[0].id)
        assertEquals(101L, types[1].id)
    }

    @Test
    fun `by id`() {
        val result =
            rest.getForEntity("/v1/types?id=103&id=100&id=101", SearchTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val types = result.body!!.types
        assertEquals(3, types.size)
        assertEquals(103L, types[0].id)
        assertEquals(100L, types[1].id)
        assertEquals(101L, types[2].id)
    }

    @Test
    fun active() {
        val result = rest.getForEntity("/v1/types?active=false", SearchTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val types = result.body!!.types
        assertEquals(1, types.size)
        assertEquals(103L, types[0].id)
    }

    @Test
    fun objectType() {
        val result = rest.getForEntity("/v1/types?object-type=ACCOUNT", SearchTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val types = result.body!!.types
        assertEquals(2, types.size)
        assertEquals(100L, types[0].id)
        assertEquals(101L, types[1].id)
    }
}

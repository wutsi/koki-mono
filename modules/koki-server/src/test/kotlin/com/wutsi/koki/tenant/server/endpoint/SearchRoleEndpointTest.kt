package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tenant.dto.SearchRoleResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchRoleEndpoint.sql"])
class SearchRoleEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/roles", SearchRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = result.body!!.roles
        assertEquals(3, roles.size)

        assertEquals("a", roles[0].name)
        assertTrue(roles[0].active)
        assertEquals("description-a", roles[0].description)

        assertEquals("b", roles[1].name)
        assertNull(roles[1].description)
        assertTrue(roles[1].active)

        assertEquals("c", roles[2].name)
        assertNull(roles[2].description)
        assertFalse(roles[2].active)
    }

    @Test
    fun `by name`() {
        val result =
            rest.getForEntity("/v1/roles?name=a&name=b", SearchRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = result.body!!.roles
        assertEquals(2, roles.size)

        assertEquals("a", roles[0].name)
        assertEquals("description-a", roles[0].description)
        assertTrue(roles[0].active)

        assertEquals("b", roles[1].name)
        assertNull(roles[1].description)
        assertTrue(roles[1].active)
    }

    @Test
    fun `by id`() {
        val result =
            rest.getForEntity("/v1/roles?id=10&id=11", SearchRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = result.body!!.roles
        assertEquals(2, roles.size)

        assertEquals(10L, roles[0].id)
        assertEquals(11L, roles[1].id)
    }

    @Test
    fun `by active`() {
        val result =
            rest.getForEntity("/v1/roles?active=false", SearchRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = result.body!!.roles
        assertEquals(1, roles.size)

        assertEquals(12L, roles[0].id)
    }

    @Test
    fun `search attribute from another tenant`() {
        val result =
            rest.getForEntity("/v1/roles?name=aa", SearchRoleResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val roles = result.body!!.roles
        assertEquals(0, roles.size)
    }
}

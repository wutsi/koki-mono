package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.AttributeType
import com.wutsi.koki.account.dto.SearchAttributeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/account/SearchAttributeEndpoint.sql"])
class SearchAttributeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/attributes", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(3, attributes.size)

        assertEquals("a", attributes[0].name)
        assertEquals("label-a", attributes[0].label)
        assertEquals(AttributeType.TEXT, attributes[0].type)
        assertTrue(attributes[0].active)

        assertEquals("b", attributes[1].name)
        assertNull(attributes[1].label)
        assertEquals(AttributeType.LONGTEXT, attributes[1].type)
        assertTrue(attributes[1].active)

        assertEquals("c", attributes[2].name)
        assertNull(attributes[2].label)
        assertEquals(AttributeType.EMAIL, attributes[2].type)
        assertFalse(attributes[2].active)
    }

    @Test
    fun `by name`() {
        val result =
            rest.getForEntity("/v1/attributes?name=a&name=b", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(2, attributes.size)

        assertEquals("a", attributes[0].name)
        assertEquals("b", attributes[1].name)
    }

    @Test
    fun `by active`() {
        val result =
            rest.getForEntity("/v1/attributes?active=true", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(2, attributes.size)

        assertEquals("a", attributes[0].name)
        assertEquals("b", attributes[1].name)
    }

    @Test
    fun `search attribute from another tenant`() {
        val result =
            rest.getForEntity("/v1/attributes?name=aa", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(0, attributes.size)
    }
}

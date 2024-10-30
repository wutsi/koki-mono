package com.wutsi.koki.tenant.server.endpoint

import com.wutsi.koki.tenant.dto.AttributeType
import com.wutsi.koki.tenant.dto.SearchAttributeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/SearchAttributeEndpoint.sql"])
class SearchAttributeEndpointTest : TenantAwareEndpointTest() {
    override fun getTenantId() = 1L

    @Test
    fun all() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result = rest.getForEntity("/v1/attributes", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(3, attributes.size)

        assertEquals("a", attributes[0].name)
        assertEquals("label-a", attributes[0].label)
        assertEquals("description-a", attributes[0].description)
        assertEquals(AttributeType.TEXT, attributes[0].type)
        assertTrue(attributes[0].active)
        assertEquals(listOf("P1", "P2"), attributes[0].choices)

        assertEquals("b", attributes[1].name)
        assertNull(attributes[1].label)
        assertNull(attributes[1].description)
        assertEquals(AttributeType.LONGTEXT, attributes[1].type)
        assertTrue(attributes[1].active)
        assertTrue(attributes[1].choices.isEmpty())

        assertEquals("c", attributes[2].name)
        assertNull(attributes[2].label)
        assertNull(attributes[2].description)
        assertEquals(AttributeType.EMAIL, attributes[2].type)
        assertFalse(attributes[2].active)
        assertTrue(attributes[2].choices.isEmpty())
    }

    @Test
    fun filter() {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val result =
            rest.getForEntity("/v1/attributes?name=a&name=b&name=aa", SearchAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attributes = result.body!!.attributes
        assertEquals(2, attributes.size)

        assertEquals("a", attributes[0].name)
        assertEquals("label-a", attributes[0].label)
        assertEquals("description-a", attributes[0].description)
        assertEquals(AttributeType.TEXT, attributes[0].type)
        assertTrue(attributes[0].active)
        assertEquals(listOf("P1", "P2"), attributes[0].choices)

        assertEquals("b", attributes[1].name)
        assertNull(attributes[1].label)
        assertNull(attributes[1].description)
        assertEquals(AttributeType.LONGTEXT, attributes[1].type)
        assertTrue(attributes[1].active)
        assertTrue(attributes[1].choices.isEmpty())
    }
}

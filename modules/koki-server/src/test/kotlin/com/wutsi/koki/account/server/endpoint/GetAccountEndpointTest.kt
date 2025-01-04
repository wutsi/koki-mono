package com.wutsi.koki.account.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.account.dto.AttributeType
import com.wutsi.koki.account.dto.GetAttributeResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/account/GetAttributeEndpoint.sql"])
class GetAttributeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/attributes/100", GetAttributeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val attribute = result.body!!.attribute
        assertEquals("a", attribute.name)
        assertEquals("label-a", attribute.label)
        assertEquals("description-a", attribute.description)
        assertEquals(listOf("P1", "P2"), attribute.choices)
        assertEquals(AttributeType.TEXT, attribute.type)
        assertTrue(attribute.active)
        assertTrue(attribute.required)
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/attributes/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.ATTRIBUTE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/attributes/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.ATTRIBUTE_NOT_FOUND, result.body?.error?.code)
    }
}

package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.contact.dto.GetContactTypeResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/GetContactTypeEndpoint.sql"])
class GetContactTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/contact-types/100", GetContactTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val contact = result.body!!.contactType
        assertEquals("a", contact.name)
        assertEquals("title-a", contact.title)
        assertEquals("description-a", contact.description)
    }

    @Test
    fun `bad id`() {
        val result = rest.getForEntity("/v1/contact-types/99999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.CONTACT_TYPE_NOT_FOUND, result.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val result = rest.getForEntity("/v1/contact-types/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.CONTACT_TYPE_NOT_FOUND, result.body?.error?.code)
    }
}

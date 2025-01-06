package com.wutsi.koki.contact.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.contact.dto.GetContactResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/contact/GetContactEndpoint.sql"])
class GetContactEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/contacts/100", GetContactResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `bad ID`() {
        val response = rest.getForEntity("/v1/contacts/9999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.CONTACT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun deleted() {
        val response = rest.getForEntity("/v1/contacts/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.CONTACT_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `another tenant`() {
        val response = rest.getForEntity("/v1/contacts/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.CONTACT_NOT_FOUND, response.body?.error?.code)
    }
}

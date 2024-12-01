package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.message.dto.GetMessageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/message/GetMessageEndpoint.sql"])
class GetMessageEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val result = rest.getForEntity("/v1/messages/100", GetMessageResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val message = result.body.message
        assertEquals("M-100", message.name)
        assertEquals("Subject", message.subject)
        assertEquals("Hello", message.body)
        assertEquals(false, message.active)
    }

    @Test
    fun `not found`() {
        val result = rest.getForEntity("/v1/messages/999999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun deleted() {
        val result = rest.getForEntity("/v1/messages/199", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `other tenant`() {
        val result = rest.getForEntity("/v1/messages/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, result.body!!.error.code)
    }
}

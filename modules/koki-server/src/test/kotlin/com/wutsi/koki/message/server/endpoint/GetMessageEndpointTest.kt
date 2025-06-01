package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.message.dto.GetMessageResponse
import com.wutsi.koki.message.dto.MessageStatus
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/message/GetMessageEndpoint.sql"])
class GetMessageEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/messages/100", GetMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = response.body!!.message
        assertEquals(11L, message.owner?.id)
        assertEquals(ObjectType.ACCOUNT, message.owner?.type)
        assertEquals("Ray Sponsible", message.senderName)
        assertEquals("ray.sponsible@gmail.com", message.senderEmail)
        assertEquals("5147580011", message.senderPhone)
        assertEquals(MessageStatus.ARCHIVED, message.status)
        assertEquals("Yo man", message.body)
        assertEquals("CA", message.country)
        assertEquals("fr", message.language)
        assertEquals(111L, message.senderAccountId)
    }

    @Test
    fun `invalid id`() {
        val response = rest.getForEntity("/v1/messages/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `invalid tenant`() {
        val response = rest.getForEntity("/v1/messages/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, response.body?.error?.code)
    }
}

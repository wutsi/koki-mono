package com.wutsi.koki.email.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.GetEmailResponse
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/email/GetEmailEndpoint.sql"])
class GetEmailEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/emails/100", GetEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val email = response.body!!.email
        assertEquals("hello", email.subject)
        assertEquals("<p>World</p>", email.body)
        assertEquals(100L, email.recipient.id)
        assertEquals(ObjectType.ACCOUNT, email.recipient.type)
        assertEquals(111L, email.senderId)
    }

    @Test
    fun `another tenant`() {
        val response = rest.getForEntity("/v1/emails/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, response.body!!.error.code)
    }
}

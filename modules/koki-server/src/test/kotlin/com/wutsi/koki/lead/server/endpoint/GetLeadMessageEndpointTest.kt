package com.wutsi.koki.lead.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.lead.dto.GetLeadMessageResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/lead/GetLeadMessageEndpoint.sql"])
class GetLeadMessageEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/lead-messages/111", GetLeadMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val lead = response.body!!.message
        assertEquals(100L, lead.leadId)
        assertEquals("Hello, I am interested in this property.", lead.content)
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/lead-messages/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `bad tenant`() {
        val response = rest.getForEntity("/v1/lead-messages/222", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, response.body?.error?.code)
    }
}

package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.message.dto.CreateMessageRequest
import com.wutsi.koki.message.dto.CreateMessageResponse
import com.wutsi.koki.message.server.dao.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/message/CreateMessageEndpoint.sql"])
class CreateMessageEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: MessageRepository

    @Test
    fun create() {
        val request = CreateMessageRequest(
            name = "TEST-100",
            subject = "This is the subject",
            body = "You have a nice body",
            description = "This is the description",
            active = true,
        )
        val result = rest.postForEntity("/v1/messages", request, CreateMessageResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val messageId = result.body!!.messageId
        val message = dao.findById(messageId).get()
        assertEquals(request.name, message.name)
        assertEquals(request.subject, message.subject)
        assertEquals(request.description, message.description)
        assertEquals(request.body, message.body)
        assertEquals(request.active, message.active)
    }

    @Test
    fun duplicate() {
        val request = CreateMessageRequest(
            name = "M-100",
            subject = "This is the subject",
            body = "You have a nice body",
            active = true,
        )
        val result = rest.postForEntity("/v1/messages", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_DUPLICATE_NAME, result.body!!.error.code)
    }
}

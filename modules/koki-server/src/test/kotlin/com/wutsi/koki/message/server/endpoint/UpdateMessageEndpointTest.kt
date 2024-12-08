package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.message.dto.UpdateMessageRequest
import com.wutsi.koki.message.server.dao.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/message/UpdateMessageEndpoint.sql"])
class UpdateMessageEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: MessageRepository

    val request = UpdateMessageRequest(
        name = "TEST-100",
        subject = "This is the subject",
        body = "You have a nice body",
        description = "This is the description",
        active = true,
    )

    @Test
    fun update() {
        val result = rest.postForEntity("/v1/messages/100", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val messageId = "100"
        val message = dao.findById(messageId).get()
        assertEquals(request.name, message.name)
        assertEquals(request.subject, message.subject)
        assertEquals(request.description, message.description)
        assertEquals(request.body, message.body)
        assertEquals(request.active, message.active)
    }

    @Test
    fun `duplicate name`() {
        val result = rest.postForEntity("/v1/messages/110", request.copy(name = "M-120"), ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_DUPLICATE_NAME, result.body!!.error.code)
    }

    @Test
    fun `not found`() {
        val result = rest.postForEntity("/v1/messages/9999", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, result.body!!.error.code)
    }

    @Test
    fun `other tenant`() {
        val result = rest.postForEntity("/v1/messages/200", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        assertEquals(ErrorCode.MESSAGE_NOT_FOUND, result.body!!.error.code)
    }
}

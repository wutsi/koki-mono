package com.wutsi.koki.message.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.UpdateMessageStatusRequest
import com.wutsi.koki.message.server.dao.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/message/UpdateMessageStatusEndpoint.sql"])
class UpdateMessageStatusEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: MessageRepository

    @Test
    fun get() {
        val request = UpdateMessageStatusRequest(status = MessageStatus.ARCHIVED)
        val response = rest.postForEntity("/v1/messages/100/status", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(100L).get()
        assertEquals(request.status, message.status)
    }
}

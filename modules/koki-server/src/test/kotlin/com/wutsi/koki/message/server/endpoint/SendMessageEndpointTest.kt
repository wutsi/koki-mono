package com.wutsi.koki.message.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.message.dto.MessageStatus
import com.wutsi.koki.message.dto.SendMessageRequest
import com.wutsi.koki.message.dto.SendMessageResponse
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.message.server.dao.MessageRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql"])
class SendMessageEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var dao: MessageRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun send() {
        val request = SendMessageRequest(
            owner = ObjectReference(id = 111, type = ObjectType.ROOM),
            senderName = "Ray Sponsible",
            senderEmail = "ray.sponsible@gmail.com",
            senderPhone = "5147580011",
            body = "Hello world"
        )
        val response = rest.postForEntity("/v1/messages", request, SendMessageResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val message = dao.findById(response.body!!.messageId).get()
        assertEquals(TENANT_ID, message.tenantId)
        assertEquals(request.owner?.id, message.ownerId)
        assertEquals(request.owner?.type, message.ownerType)
        assertEquals(request.senderName, message.senderName)
        assertEquals(request.senderEmail, message.senderEmail)
        assertEquals(request.senderPhone, message.senderPhone)
        assertEquals(MessageStatus.NEW, message.status)
        assertEquals(request.body, message.body)

        val event = argumentCaptor<MessageSentEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(request.owner, event.firstValue.owner)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(message.id, event.firstValue.messageId)
    }
}

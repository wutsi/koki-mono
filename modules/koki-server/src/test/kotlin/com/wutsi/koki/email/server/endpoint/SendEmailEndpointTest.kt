package com.wutsi.koki.email.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.email.dto.Recipient
import com.wutsi.koki.email.dto.SendEmailRequest
import com.wutsi.koki.email.dto.SendEmailResponse
import com.wutsi.koki.email.server.dao.AttachmentRepository
import com.wutsi.koki.email.server.dao.EmailOwnerRepository
import com.wutsi.koki.email.server.dao.EmailRepository
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingException
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/email/SendEmailEndpoint.sql"])
class SendEmailEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: EmailRepository

    @Autowired
    private lateinit var attachmentDao: AttachmentRepository

    @Autowired
    private lateinit var ownerDao: EmailOwnerRepository

    @MockitoBean
    private lateinit var messagingService: MessagingService

    @MockitoBean
    private lateinit var messagingServiceBuilder: MessagingServiceBuilder

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(messagingService).whenever(messagingServiceBuilder).build(any(), any())
        doReturn("xxxx").whenever(messagingService).send(any())
    }

    @Test
    fun `send to account`() {
        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(id = 100, type = ObjectType.ACCOUNT),
            owner = ObjectReference(id = 111, type = ObjectType.TAX),
            attachmentFileIds = listOf(100, 101)
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id).get()
        assertEquals(request.subject, email.subject)
        assertEquals("<p>This is an example of email</p>", email.body)
        assertEquals(request.recipient.id, email.recipientId)
        assertEquals(request.recipient.type, email.recipientType)
        assertEquals(USER_ID, email.senderId)
        assertEquals(TENANT_ID, email.tenantId)

        val emailOwners = ownerDao.findByEmailId(id)
        assertEquals(1, emailOwners.size)
        assertEquals(request.owner!!.id, emailOwners[0].ownerId)
        assertEquals(request.owner!!.type, emailOwners[0].ownerType)

        val attachments = attachmentDao.findByEmailId(id)
        assertEquals(2, attachments.size)
        assertEquals(100L, attachments[0].fileId)
        assertEquals(101L, attachments[1].fileId)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())
        assertTrue(msg.firstValue.body.contains(request.body))
        assertEquals(
            "<table> <tr><td>test</td></tr> <tr><td>${request.body}</td></tr> </table>",
            msg.firstValue.body,
        )
        assertEquals("Ray Inc", msg.firstValue.recipient.displayName)
        assertEquals("info@ray-inc.com", msg.firstValue.recipient.email)
        assertEquals("text/html", msg.firstValue.mimeType)
        assertEquals(request.attachmentFileIds.size, msg.firstValue.attachments.size)
    }

    @Test
    fun `send to account without account`() {
        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(id = 101, type = ObjectType.ACCOUNT),
        )
        val response = rest.postForEntity("/v1/emails", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.EMAIL_RECIPIENT_EMAIL_MISSING, response.body!!.error.code)
    }

    @Test
    fun `send to contact`() {
        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(id = 110, type = ObjectType.CONTACT),
            owner = null
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id).get()
        assertEquals(request.subject, email.subject)
        assertEquals(request.body, email.body)
        assertEquals(request.recipient.id, email.recipientId)
        assertEquals(request.recipient.type, email.recipientType)
        assertEquals(USER_ID, email.senderId)
        assertEquals(TENANT_ID, email.tenantId)

        val emailOwners = ownerDao.findByEmailId(id)
        assertEquals(0, emailOwners.size)

        val attachments = attachmentDao.findByEmailId(id)
        assertEquals(0, attachments.size)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())
        assertEquals(request.subject, msg.firstValue.subject)
        assertTrue(msg.firstValue.body.contains(request.body))
        assertEquals("Ray Sponsible", msg.firstValue.recipient.displayName)
        assertEquals("ray.sponsible@gmail.com", msg.firstValue.recipient.email)
        assertEquals("text/html", msg.firstValue.mimeType)
    }

    @Test
    fun `send to contact without account`() {
        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(id = 120, type = ObjectType.CONTACT),
        )
        val response = rest.postForEntity("/v1/emails", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.EMAIL_RECIPIENT_EMAIL_MISSING, response.body!!.error.code)
    }

    @Test
    fun `email not saved on MessagingException`() {
        doThrow(MessagingException("failed")).whenever(messagingService).send(any())

        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(id = 100, type = ObjectType.ACCOUNT),
            owner = ObjectReference(id = 777777L, type = ObjectType.TAX)
        )
        val response = rest.postForEntity("/v1/emails", request, ErrorResponse::class.java)
        assertEquals(ErrorCode.EMAIL_DELIVERY_FAILED, response.body!!.error.code)

        val emails = ownerDao.findByOwnerIdAndOwnerType(request.owner!!.id, request.owner!!.type)
        assertEquals(0, emails.size)
    }
}

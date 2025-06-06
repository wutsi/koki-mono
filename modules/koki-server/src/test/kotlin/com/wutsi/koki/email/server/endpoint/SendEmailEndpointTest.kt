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
import com.wutsi.koki.platform.messaging.MessagingNotConfiguredException
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.translation.TranslationService
import com.wutsi.koki.translation.server.service.TranslationServiceProvider
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.io.IOException
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

    @MockitoBean
    private lateinit var storageService: StorageService

    @MockitoBean
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    @MockitoBean
    private lateinit var translationServiceProvider: TranslationServiceProvider

    private val translationService: TranslationService = mock<TranslationService>()

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(messagingService).whenever(messagingServiceBuilder).build(any())
        doReturn("xxxx").whenever(messagingService).send(any())

        doReturn(storageService).whenever(storageServiceBuilder).build(any())

        doReturn(translationService).whenever(translationServiceProvider).get(any())
    }

    @Test
    fun `send to account`() {
        val request = SendEmailRequest(
            subject = "Hello man - Invoice #{{invoiceNumber}}",
            body = "<p>Hello {{recipientName}}<br/>This is an example of email</p>",
            recipient = Recipient(
                id = 100,
                type = ObjectType.ACCOUNT,
                email = "info@ray-inc.com",
                displayName = "Ray Inc"
            ),
            owner = ObjectReference(id = 111, type = ObjectType.TAX),
            attachmentFileIds = listOf(100, 101),
            data = mapOf(
                "invoiceNumber" to "1111"
            ),
            store = true,
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id).get()
        assertEquals("Hello man - Invoice #${request.data["invoiceNumber"]}", email.subject)
        assertEquals("<p>Hello ${request.recipient.displayName}<br/>This is an example of email</p>", email.body)
        assertEquals("Hello ${request.recipient.displayName} This is an example of email", email.summary)
        assertEquals(request.recipient.id, email.recipientId)
        assertEquals(request.recipient.type, email.recipientType)
        assertEquals(request.recipient.email, email.recipientEmail)
        assertEquals(request.recipient.displayName, email.recipientDisplayName)
        assertEquals(request.attachmentFileIds.size, email.attachmentCount)
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
        assertTrue(msg.firstValue.body.contains(email.body))
        assertEquals(email.subject, msg.firstValue.subject)
        assertEquals(request.recipient.displayName, msg.firstValue.recipient.displayName)
        assertEquals(request.recipient.email, msg.firstValue.recipient.email)
        assertEquals("", msg.firstValue.sender?.email)
        assertEquals("Business Inc", msg.firstValue.sender?.displayName)
        assertEquals("text/html", msg.firstValue.mimeType)
        assertEquals(request.attachmentFileIds.size, msg.firstValue.attachments.size)
    }

    @Test
    fun `send to contact`() {
        val request = SendEmailRequest(
            subject = "Hello man",
            body = """
                <p>
                    <b>Lorem Ipsum</b> is simply dummy text of the printing and typesetting industry.
                    Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
                    when an unknown printer took a galley of type and scrambled it to make a type specimen book.
                    It has survived not only five centuries, but also the leap into electronic typesetting,
                    remaining essentially unchanged.
                    It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                    and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                </p>
                <p>
                    Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...
                </p>
                <p>
                    There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...
                </p>
            """.trimIndent(),
            recipient = Recipient(
                id = 110,
                type = ObjectType.CONTACT,
                email = "ray.sponsible@gmail.com",
                displayName = "Ray Sponsible"
            ),
            owner = null
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id).get()
        assertEquals(request.subject, email.subject)
        assertEquals(request.body, email.body)
        assertEquals(
            """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has...
            """.trimIndent(),
            email.summary,
        )
        assertEquals(request.recipient.id, email.recipientId)
        assertEquals(request.recipient.type, email.recipientType)
        assertEquals(request.attachmentFileIds.size, email.attachmentCount)
        assertEquals(USER_ID, email.senderId)
        assertEquals(TENANT_ID, email.tenantId)

        val emailOwners = ownerDao.findByEmailId(id)
        assertEquals(0, emailOwners.size)

        val attachments = attachmentDao.findByEmailId(id)
        assertEquals(0, attachments.size)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())
        assertEquals(request.subject, msg.firstValue.subject)
        assertTrue(msg.firstValue.body.contains(email.body))
        assertEquals(request.recipient.displayName, msg.firstValue.recipient.displayName)
        assertEquals(request.recipient.email, msg.firstValue.recipient.email)
        assertEquals("text/html", msg.firstValue.mimeType)
    }

    @Test
    fun `send without recipientId`() {
        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(
                id = null,
                type = ObjectType.UNKNOWN,
                email = "info@ray-inc.com",
                displayName = "Ray Inc"
            ),
            owner = ObjectReference(id = 111, type = ObjectType.TAX),
            attachmentFileIds = listOf(100, 101)
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id).get()
        assertEquals(request.subject, email.subject)
        assertEquals("<p>This is an example of email</p>", email.body)
        assertEquals("This is an example of email", email.summary)
        assertEquals(request.recipient.id, email.recipientId)
        assertEquals(request.recipient.type, email.recipientType)
        assertEquals(request.recipient.email, email.recipientEmail)
        assertEquals(request.recipient.displayName, email.recipientDisplayName)
        assertEquals(request.attachmentFileIds.size, email.attachmentCount)
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
        assertEquals(request.recipient.displayName, msg.firstValue.recipient.displayName)
        assertEquals(request.recipient.email, msg.firstValue.recipient.email)
        assertEquals("text/html", msg.firstValue.mimeType)
        assertEquals(request.attachmentFileIds.size, msg.firstValue.attachments.size)
    }

    @Test
    fun `send without storing `() {
        val request = SendEmailRequest(
            subject = "Hello man - Invoice #123",
            body = "<p>Hello Ray<br/>This is an example of email</p>",
            recipient = Recipient(
                id = 100,
                type = ObjectType.ACCOUNT,
                email = "info@ray-inc.com",
                displayName = "Ray Inc"
            ),
            owner = ObjectReference(id = 111, type = ObjectType.TAX),
            attachmentFileIds = listOf(100, 101),
            data = mapOf(
                "invoiceNumber" to "1111"
            ),
            store = false,
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id)
        assertEquals(true, email.isEmpty)

        val emailOwners = ownerDao.findByEmailId(id)
        assertEquals(0, emailOwners.size)

        val attachments = attachmentDao.findByEmailId(id)
        assertEquals(0, attachments.size)

        val msg = argumentCaptor<Message>()
        verify(messagingService).send(msg.capture())
        assertTrue(msg.firstValue.body.contains(request.body))
        assertEquals(request.subject, msg.firstValue.subject)
        assertEquals(request.recipient.displayName, msg.firstValue.recipient.displayName)
        assertEquals(request.recipient.email, msg.firstValue.recipient.email)
        assertEquals("", msg.firstValue.sender?.email)
        assertEquals("Business Inc", msg.firstValue.sender?.displayName)
        assertEquals("text/html", msg.firstValue.mimeType)
        assertEquals(request.attachmentFileIds.size, msg.firstValue.attachments.size)
    }

    @Test
    fun `email not saved on MessagingException`() {
        doThrow(MessagingException("failed")).whenever(messagingService).send(any())

        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(email = "messaging-exception@gmail.com"),
            owner = ObjectReference(id = 777777L, type = ObjectType.TAX)
        )
        val response = rest.postForEntity("/v1/emails", request, ErrorResponse::class.java)
        assertEquals(ErrorCode.EMAIL_DELIVERY_FAILED, response.body!!.error.code)

        val emails = dao.findByRecipientEmail(request.recipient.email)
        assertEquals(0, emails.size)

        val owners = ownerDao.findByOwnerIdAndOwnerType(request.owner!!.id, request.owner!!.type)
        assertEquals(0, owners.size)
    }

    @Test
    fun `no SMTP`() {
        doThrow(MessagingNotConfiguredException("failed")).whenever(messagingService).send(any())

        val request = SendEmailRequest(
            subject = "Hello man",
            body = "<p>This is an example of email</p>",
            recipient = Recipient(email = "foo@gmail.com"),
            owner = ObjectReference(id = 777777L, type = ObjectType.TAX)
        )
        val response = rest.postForEntity("/v1/emails", request, ErrorResponse::class.java)
        assertEquals(ErrorCode.EMAIL_SMTP_NOT_CONFIGURED, response.body!!.error.code)

        val emails = ownerDao.findByOwnerIdAndOwnerType(request.owner!!.id, request.owner!!.type)
        assertEquals(0, emails.size)
    }

    @Test
    fun `send with invalid attachment`() {
        doThrow(IOException()).whenever(storageService).get(any(), any())

        val request = SendEmailRequest(
            subject = "Hello man - Invoice #{{invoiceNumber}}",
            body = "<p>Hello {{recipientName}}<br/>This is an example of email</p>",
            recipient = Recipient(
                email = "invalid-attachment@ray-inc.com",
                displayName = "Ray Inc"
            ),
            owner = ObjectReference(id = 111, type = ObjectType.TAX),
            attachmentFileIds = listOf(199),
        )
        val response = rest.postForEntity("/v1/emails", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(ErrorCode.EMAIL_DELIVERY_FAILED, response.body!!.error.code)

        val emails = dao.findByRecipientEmail(request.recipient.email)
        assertEquals(0, emails.size)

        val owners = ownerDao.findByOwnerIdAndOwnerType(request.owner!!.id, request.owner!!.type)
        assertEquals(0, owners.size)
    }

    @Test
    fun translate() {
        doReturn("Ze subject")
            .doReturn("Ze body")
            .whenever(translationService).translate(any(), any())

        val request = SendEmailRequest(
            subject = "Invoice #{{invoiceNumber}} is ready",
            body = """
                Dear {{customerName}},

                <br/><br/>
                I hope this email finds you well.

                <br/><br/>
                We are writing to confirm that we have successfully received your payment of
                <b>{{paymentAmount}}</b> for the invoice #{{invoiceNumber}}.
                Thank you for your prompt payment!

                <br/><br/>
                Below are the details of the transaction for your records:
                <ul>
                    <li>Invoice Number: {{invoiceNumber}}</li>
                    <li>Amount Paid: {{paymentAmount}}</li>
                    <li>Payment Method: {{paymentMethod}}</li>
                    <li>Payment Method: {{paymentDate}}</li>
                </ul>

                <br/><br/>
                We truly value your business and look forward to serving you again in the future.
                If there’s anything else we can assist you with, please let us know!

                <br/><br/>
                Thank you once again for choosing {{businessName}}.

                <br/><br/>
                Best regards
            """.trimIndent(),
            recipient = Recipient(
                id = 110,
                type = ObjectType.CONTACT,
                email = "ray.sponsible@gmail.com",
                displayName = "Ray Sponsible",
                language = "fr",
            ),
            owner = null,
            data = mapOf(
                "invoiceNumber" to "1111",
                "customerName" to "Ray Sponsible",
                "paymentAmount" to "$1,500",
                "paymentMethod" to "Credit Card",
                "paymentDate" to "23 Jun 2024",
                "businessName" to "Windo LLC"
            )
        )
        val response = rest.postForEntity("/v1/emails", request, SendEmailResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val id = response.body!!.emailId
        val email = dao.findById(id).get()
        assertEquals("Ze body", email.summary)
        assertEquals("Ze subject", email.subject)
        assertEquals("Ze body", email.body)

        verify(messagingService).send(any())

        println("Subject: ${email.subject}")
        println("Summary: ${email.summary}")
        println("\n${email.body}")
    }
}

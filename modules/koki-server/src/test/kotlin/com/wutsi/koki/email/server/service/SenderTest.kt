package com.wutsi.koki.email.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.BusinessEntity
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.BusinessService
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.tika.language.detect.LanguageDetector
import org.apache.tika.language.detect.LanguageResult
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class SenderTest {
    private val businessService = mock<BusinessService>()
    private val filterSet = mock<EmailFilterSet>()
    private val configurationService = mock<ConfigurationService>()
    private val messagingServiceBuilder = mock<MessagingServiceBuilder>()
    private val languageDetector = mock<LanguageDetector>()
    private val sender = Sender(
        businessService,
        filterSet,
        configurationService,
        messagingServiceBuilder,
        languageDetector,
    )

    private val recipient = UserEntity(id = 11L, tenantId = 1L, email = "ray.sponsible@gmail.com", username = "ray")
    private val business = BusinessEntity(companyName = "Koki")
    private val configurations = listOf(
        ConfigurationEntity(
            name = ConfigurationName.SMTP_FROM_PERSONAL,
            value = "Koki"
        ),
        ConfigurationEntity(
            name = ConfigurationName.SMTP_FROM_ADDRESS,
            value = "no-reply@koki.com"
        ),
    )
    private val attachments = listOf(
        File("/u/v.txt"),
        File("/x/y.txt")
    )
    private val messageService = mock<MessagingService>()

    @BeforeEach
    fun setUp() {
        doReturn(business).whenever(businessService).getOrNull(any())
        doReturn(messageService).whenever(messagingServiceBuilder).build(any())
        doReturn(configurations).whenever(configurationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
        doReturn("HELLO WORLD").whenever(filterSet).filter(any(), any())

        val result = mock<LanguageResult>()
        doReturn("fr").whenever(result).language
        doReturn(result).whenever(languageDetector).detect(any())
    }

    @Test
    fun send() {
        sender.send(
            recipient = recipient,
            subject = "Yo man",
            body = "Hello world",
            attachments = attachments,
            tenantId = 1L
        )

        val msg = argumentCaptor<Message>()
        verify(messageService).send(msg.capture())

        assertEquals("HELLO WORLD", msg.firstValue.body)
        assertEquals("Yo man", msg.firstValue.subject)
        assertEquals(attachments, msg.firstValue.attachments)
        assertEquals(recipient.email, msg.firstValue.recipient.email)
        assertEquals(recipient.displayName, msg.firstValue.recipient.displayName)
        assertEquals("fr", msg.firstValue.language)
        assertEquals("text/html", msg.firstValue.mimeType)
        assertEquals("Koki", msg.firstValue.sender?.displayName)
        assertEquals("no-reply@koki.com", msg.firstValue.sender?.email)
    }

    @Test
    fun `no sender configured`() {
        doReturn(emptyList<ConfigurationEntity>()).whenever(configurationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        sender.send(
            recipient = recipient,
            subject = "Yo man",
            body = "Hello world",
            attachments = attachments,
            tenantId = 1L
        )

        val msg = argumentCaptor<Message>()
        verify(messageService).send(msg.capture())

        assertEquals("HELLO WORLD", msg.firstValue.body)
        assertEquals("Yo man", msg.firstValue.subject)
        assertEquals(attachments, msg.firstValue.attachments)
        assertEquals(recipient.email, msg.firstValue.recipient.email)
        assertEquals(recipient.displayName, msg.firstValue.recipient.displayName)
        assertEquals("fr", msg.firstValue.language)
        assertEquals("text/html", msg.firstValue.mimeType)
        assertEquals(business.companyName, msg.firstValue.sender?.displayName)
        assertEquals("", msg.firstValue.sender?.email)
    }
}

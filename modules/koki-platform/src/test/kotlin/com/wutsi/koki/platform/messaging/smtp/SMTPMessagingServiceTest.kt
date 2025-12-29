package com.wutsi.koki.platform.messaging.smtp

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.MessagingException
import com.wutsi.koki.platform.messaging.Party
import com.wutsi.koki.tenant.dto.ConfigurationName
import jakarta.mail.Message.RecipientType
import jakarta.mail.Session
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SMTPMessagingServiceTest {
    private val port = 8025
    private val config = mapOf(
        ConfigurationName.SMTP_TYPE to SMTPType.EXTERNAL.name,
        ConfigurationName.SMTP_PORT to port.toString(),
        ConfigurationName.SMTP_HOST to "localhost",
        ConfigurationName.SMTP_USERNAME to "ray.sponsible",
        ConfigurationName.SMTP_PASSWORD to "secret",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki"
    )

    private lateinit var smtp: GreenMail
    private val builder = SMTPMessagingServiceBuilder(
        session = mock<Session>(),
        from = "no-reply@xxxx.com"
    )
    private val message = Message(
        sender = Party(email = "no-reply@tenant1.com", displayName = "Tenant1"),
        recipient = Party(email = "ray.sponsible@gmail.com", displayName = "Ray Sponsible"),
        subject = "Hello",
        body = "Yo man",
        language = "en",
        mimeType = "text/html",
    )

    @BeforeTest
    fun setUp() {
        smtp = GreenMail(ServerSetup.SMTP.port(port))
        smtp.setUser(config[ConfigurationName.SMTP_USERNAME], config[ConfigurationName.SMTP_PASSWORD])
        smtp.start()
    }

    @AfterTest
    fun tearDown() {
        if (smtp.isRunning) {
            smtp.stop()
        }
    }

    @Test
    fun send() {
        val sender = builder.build(config)
        val result = sender.send(message)

        assertEquals("", result)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        assertEquals(message.subject, messages[0].subject)
        assertEquals(message.body, messages[0].content.toString())
        assertEquals(1, messages[0].getRecipients(RecipientType.TO).size)
        assertEquals(
            "${message.recipient.displayName} <${message.recipient.email}>",
            messages[0].getRecipients(RecipientType.TO)[0].toString()
        )
        assertEquals(
            "${message.sender?.displayName} <${config[ConfigurationName.SMTP_FROM_ADDRESS]}>",
            messages[0].from[0].toString()
        )

        val headers = messages[0].allHeaders
            .toList()
            .associate { header -> header.name to header.value }
        assertEquals(message.language, headers["Content-Language"])
        assertEquals(true, headers["Content-Type"]?.startsWith(message.mimeType))
    }

    @Test
    fun sendToRecipientWithoutDisplayName() {
        val sender = builder.build(config)
        val result = sender.send(
            message.copy(recipient = message.recipient.copy(displayName = null))
        )

        assertEquals("", result)

        val messages = smtp.receivedMessages
        assertEquals(
            message.recipient.email,
            messages[0].getRecipients(RecipientType.TO)[0].toString()
        )
    }

    @Test
    fun sendWithoutLanguage() {
        val sender = builder.build(config)
        val result = sender.send(message.copy(language = null))

        assertEquals("", result)
    }

    @Test
    fun sendWithAttachment() {
        val file = File.createTempFile("test", ".txt")
        file.writeText("Hello world")

        val xmessage = message.copy(attachments = listOf(file))

        val sender = builder.build(config)
        val result = sender.send(xmessage)

        assertEquals("", result)

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
    }

    @Test
    fun `no sender`() {
        val sender = builder.build(config)
        sender.send(message.copy(sender = null))

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        assertEquals(
            "${config[ConfigurationName.SMTP_FROM_PERSONAL]} <${config[ConfigurationName.SMTP_FROM_ADDRESS]}>",
            messages[0].from[0].toString()
        )
    }

    @Test
    fun `sender display name is null`() {
        val sender = builder.build(config)
        sender.send(message.copy(sender = Party(displayName = null)))

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        assertEquals(
            "${config[ConfigurationName.SMTP_FROM_PERSONAL]} <${config[ConfigurationName.SMTP_FROM_ADDRESS]}>",
            messages[0].from[0].toString()
        )
    }

    @Test
    fun `sender display name is empty`() {
        val sender = builder.build(config)
        sender.send(message.copy(sender = Party(displayName = "")))

        val messages = smtp.receivedMessages
        assertTrue(messages.isNotEmpty())
        assertEquals(
            "${config[ConfigurationName.SMTP_FROM_PERSONAL]} <${config[ConfigurationName.SMTP_FROM_ADDRESS]}>",
            messages[0].from[0].toString()
        )
    }

    @Test
    fun error() {
        // Stop
        smtp.stop()

        val sender = builder.build(config)
        assertThrows<MessagingException> { sender.send(message) }
    }
}

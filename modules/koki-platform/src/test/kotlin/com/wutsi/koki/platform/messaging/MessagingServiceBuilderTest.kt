package com.wutsi.koki.platform.messaging

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingService
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagingServiceBuilderTest {
    private val smtpBuilder = mock<SMTPMessagingServiceBuilder>()
    val smtp = mock<SMTPMessagingService>()

    private val config = mapOf("a" to "aa")
    private val builder = MessagingServiceBuilder(smtpBuilder)

    @BeforeEach
    fun setup() {
        doReturn(smtp).whenever(smtpBuilder).build(config)
    }

    @Test
    fun `SMTP messaging`() {
        val messaging = builder.build(config)
        assertEquals(smtp, messaging)
    }
}

package com.wutsi.koki.platform.messaging.smtp

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import jakarta.mail.Session
import jakarta.mail.Transport
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.boot.health.contributor.Status
import kotlin.test.Test
import kotlin.test.assertEquals

class SMTPHealthIndicatorTest {
    private val session = mock<Session>()
    private val transport = mock<Transport>()
    private val health = SMTPHealthIndicator(session)

    @BeforeEach
    fun setUp() {
        doReturn(transport).whenever(session).getTransport(any<String>())
    }

    @Test
    fun up() {
        assertEquals(Status.UP, health.health().status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException::class).whenever(transport).connect()

        assertEquals(Status.DOWN, health.health().status)
    }
}

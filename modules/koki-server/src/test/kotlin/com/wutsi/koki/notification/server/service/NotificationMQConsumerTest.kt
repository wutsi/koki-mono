package com.wutsi.koki.notification.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationMQConsumerTest {
    private val worker1 = mock<NotificationWorker>()
    private val worker2 = mock<NotificationWorker>()
    private val worker3 = mock<NotificationWorker>()
    private val consumer = NotificationMQConsumer()

    @BeforeEach
    fun setUp() {
        doReturn(false).whenever(worker1).notify(any())
        doReturn(true).whenever(worker1).notify(any<NotificationEvent1>())

        doReturn(false).whenever(worker2).notify(any())
        doReturn(true).whenever(worker2).notify(any<NotificationEvent2>())

        doReturn(false).whenever(worker3).notify(any())
        doReturn(true).whenever(worker3).notify(any<NotificationEvent3>())

        consumer.register(worker1)
        consumer.register(worker2)
        consumer.register(worker3)
    }

    @Test
    fun consume() {
        val event = NotificationEvent2()
        val result = consumer.consume(event)
        assertEquals(true, result)

        verify(worker1).notify(event)
        verify(worker2).notify(event)
        verify(worker3, never()).notify(event)
    }

    @Test
    fun `unsupported event`() {
        val event = NotificationEvent4()
        val result = consumer.consume(event)
        assertEquals(false, result)

        verify(worker1).notify(event)
        verify(worker2).notify(event)
        verify(worker3).notify(event)
    }

    @Test
    fun unregister() {
        consumer.unregister(worker1)
        consumer.unregister(worker2)
        consumer.unregister(worker3)

        val event = NotificationEvent2()
        val result = consumer.consume(event)
        assertEquals(false, result)

        verify(worker1, never()).notify(event)
        verify(worker2, never()).notify(event)
        verify(worker3, never()).notify(event)
    }
}

data class NotificationEvent1(val value: String = "v1")
data class NotificationEvent2(val value: String = "v2")
data class NotificationEvent3(val value: String = "v3")
data class NotificationEvent4(val value: String = "v4")

package com.wutsi.koki.lead.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.lead.dto.event.LeadMessageReceivedEvent
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class LeadMQConsumerTest {
    private val leadMessageReceivedEventHandler = mock<LeadMessageReceivedEventHandler>()
    private val consumer = LeadMQConsumer(
        leadMessageReceivedEventHandler = leadMessageReceivedEventHandler,
    )

    @BeforeEach
    fun setUp() {
        doReturn(true).whenever(leadMessageReceivedEventHandler).handle(any())
    }

    @Test
    fun leadMessageReceived() {
        val event = LeadMessageReceivedEvent()
        val result = consumer.consume(event)

        assertEquals(true, result)
        verify(leadMessageReceivedEventHandler).handle(event)
    }

    @Test
    fun `returns handler result when handling LeadMessageReceivedEvent`() {
        doReturn(false).whenever(leadMessageReceivedEventHandler).handle(any())

        val event = LeadMessageReceivedEvent()
        val result = consumer.consume(event)

        assertEquals(false, result)
        verify(leadMessageReceivedEventHandler).handle(event)
    }

    @Test
    fun `returns false for unsupported event type`() {
        val event = FileUploadedEvent()
        val result = consumer.consume(event)

        assertEquals(false, result)
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }

    @Test
    fun `returns false for unknown event type`() {
        val event = object {}
        val result = consumer.consume(event)

        assertEquals(false, result)
        verify(leadMessageReceivedEventHandler, never()).handle(any())
    }
}

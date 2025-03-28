package com.wutsi.koki.platform.mq.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.GetResponse
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.Test

class RabbitMQPublisherTest {
    private val storage = mock<StorageService>()
    private val storageBuilder = mock<StorageServiceBuilder>()
    private val channel = mock<Channel>()
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val exchangeName: String = "koki"
    private val maxRetries: Int = 24
    private val ttl: Int = 86400

    private val publisher = RabbitMQPublisher(channel, objectMapper, storageBuilder, exchangeName, maxRetries, ttl)
    private val queue = "test-queue"
    private val dlq = "test-dlq"

    private val properties = mock<BasicProperties>()
    private val enveloppe = mock<Envelope>()
    private val response = mock<GetResponse>()
    private val deliveryTag = 111L
    private val body = "{\"foo\":\"bar\"}"

    @BeforeEach()
    fun setUp() {
        doReturn(mapOf("x-retries" to 1)).whenever(properties).headers
        doReturn("application/json").whenever(properties).contentType
        doReturn(properties).whenever(response).props

        doReturn(deliveryTag).whenever(enveloppe).deliveryTag
        doReturn(enveloppe).whenever(response).envelope

        doReturn(body.toByteArray()).whenever(response).body

        doReturn(storage).whenever(storageBuilder).default()
    }

    @Test
    fun publish() {
        val event = TestEvent("foo", 11)
        publisher.publish(event)

        val data = argumentCaptor<ByteArray>()
        val properties = argumentCaptor<BasicProperties>()
        verify(channel).basicPublish(
            eq(exchangeName),
            eq(""),
            properties.capture(),
            data.capture(),
        )

        assertEquals("application/json", properties.firstValue.contentType)
        assertEquals("utf-8", properties.firstValue.contentEncoding)
        assertEquals((ttl * 1000).toString(), properties.firstValue.expiration)
        assertEquals(0, properties.firstValue.headers["x-retries"])
        assertEquals(maxRetries, properties.firstValue.headers["x-max-retries"])

        val json = String(data.firstValue)
        val evt = objectMapper.readValue(json, RabbitMQEvent::class.java)
        assertEquals(TestEvent::class.java.name, evt.classname)
        assertEquals("{\"name\":\"foo\",\"value\":11}", evt.payload)
    }

    @Test
    fun `publish fails`() {
        doThrow(IllegalStateException::class).whenever(channel).basicPublish(any(), any(), any(), any())

        val event = TestEvent("foo", 11)
        publisher.publish(event)
    }

    @Test
    fun `process DLQ - empty`() {
        doReturn(null).whenever(channel).basicGet(dlq, false)

        publisher.processDlq(queue, dlq)

        verify(channel, never()).basicReject(any(), any())
        verify(channel, never()).basicPublish(any(), any(), any(), any())
        verify(channel, never()).basicAck(any(), any())
    }

    @Test
    fun `process DLQ`() {
        doReturn(response)
            .doReturn(null)
            .whenever(channel).basicGet(dlq, false)

        publisher.processDlq(queue, dlq)

        verify(channel, never()).basicReject(any(), any())

        val props = argumentCaptor<BasicProperties>()
        verify(channel).basicPublish(eq(""), eq(queue), props.capture(), eq(body.toByteArray()))
        verify(channel).basicAck(deliveryTag, false)

        assertEquals(2, props.firstValue.headers["x-retries"])
        assertEquals(maxRetries, props.firstValue.headers["x-max-retries"])
    }

    @Test
    fun `process DLQ - expired`() {
        doReturn(mapOf("x-retries" to (maxRetries + 1))).whenever(properties).headers
        doReturn(response)
            .doReturn(null)
            .whenever(channel).basicGet(dlq, false)

        publisher.processDlq(queue, dlq)

        verify(channel).basicReject(deliveryTag, false)
        verify(channel, never()).basicPublish(any(), any(), any(), any())
        verify(channel, never()).basicAck(any(), any())

        val today = SimpleDateFormat("yyyy/MM/dd").format(Date())
        verify(storage).store(
            eq("rabbitmq/queues/$dlq/$today/$deliveryTag.json"),
            any(),
            eq("application/json"),
            eq(body.length.toLong())
        )
    }

    @Test
    fun `process DLQ - expired - storage error`() {
        doThrow(IllegalStateException::class).whenever(storageBuilder).default()

        doReturn("text/plain").whenever(properties).contentType
        doReturn(mapOf("x-retries" to (maxRetries + 1))).whenever(properties).headers
        doReturn(response)
            .doReturn(null)
            .whenever(channel).basicGet(dlq, false)

        publisher.processDlq(queue, dlq)

        verify(channel).basicReject(deliveryTag, false)
        verify(channel, never()).basicPublish(any(), any(), any(), any())
        verify(channel, never()).basicAck(any(), any())

        verify(storage, never()).store(any(), anyOrNull(), anyOrNull(), anyOrNull())
    }
}

package com.wutsi.koki.event.server.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test

class RabbitMQConsumerTest {
    private val channel = mock<Channel>()
    private val handler = mock<RabbitMQHandler>()
    private val envelope = mock<Envelope>()
    private val properties = mock<AMQP.BasicProperties>()
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val deliveryTag = 111L
    private val consumer = RabbitMQConsumer(objectMapper, handler, channel)

    private val payload = TestEvent("foo", 11)
    val json = objectMapper.writeValueAsString(
        RabbitMQEvent(
            classname = payload::class.java.name,
            payload = objectMapper.writeValueAsString(payload),
        )
    )

    @BeforeEach
    fun setUp() {
        doReturn(deliveryTag).whenever(envelope).deliveryTag
    }

    @Test
    fun consume() {
        consumer.handleDelivery("foo", envelope, properties, json.toByteArray())

        verify(handler).handle(payload)
        verify(channel).basicAck(deliveryTag, false)
    }

    @Test
    fun error() {
        doThrow(IllegalStateException::class).whenever(handler).handle(any<Object>())

        consumer.handleDelivery("foo", envelope, properties, json.toByteArray())

        verify(channel).basicReject(deliveryTag, false)
    }
}

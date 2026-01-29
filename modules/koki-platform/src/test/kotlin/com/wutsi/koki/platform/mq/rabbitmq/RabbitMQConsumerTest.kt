package com.wutsi.koki.platform.mq.rabbitmq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Envelope
import com.wutsi.koki.platform.mq.Consumer
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import java.util.concurrent.Executors
import kotlin.test.Test

class RabbitMQConsumerTest {
    private val channel = mock<Channel>()
    private val delegate = mock<Consumer>()
    private val envelope = mock<Envelope>()
    private val properties = mock<AMQP.BasicProperties>()
    private val jsonMapper: JsonMapper = JsonMapper()
    private val deliveryTag = 111L
    private val executorService = Executors.newFixedThreadPool(1)
    private val consumer = RabbitMQConsumer(jsonMapper, delegate, executorService, channel)

    private val payload = TestEvent("foo", 11)
    val json = jsonMapper.writeValueAsString(
        RabbitMQEvent(
            classname = payload::class.java.name,
            payload = jsonMapper.writeValueAsString(payload),
        )
    )

    @BeforeEach
    fun setUp() {
        doReturn(deliveryTag).whenever(envelope).deliveryTag
    }

    @Test
    fun consume() {
        consumer.handleDelivery("foo", envelope, properties, json.toByteArray())

        Thread.sleep(500)
        verify(delegate).consume(payload)
        verify(channel).basicAck(deliveryTag, false)
    }

    @Test
    fun error() {
        doThrow(IllegalStateException::class).whenever(delegate).consume(any<Object>())

        consumer.handleDelivery("foo", envelope, properties, json.toByteArray())

        Thread.sleep(500)
        verify(channel).basicReject(deliveryTag, false)
    }
}

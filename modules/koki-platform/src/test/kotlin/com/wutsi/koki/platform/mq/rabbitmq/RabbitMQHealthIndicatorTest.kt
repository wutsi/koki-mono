package com.wutsi.koki.platform.mq.rabbitmq

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status

class RabbitMQHealthIndicatorTest {
    private lateinit var channel: Channel
    private lateinit var connection: Connection
    private lateinit var properties: Map<String, Any>
    private lateinit var health: HealthIndicator

    @BeforeEach
    fun setUp() {
        channel = mock()
        connection = mock()
        properties = mapOf("version" to "1.1")

        doReturn(connection).whenever(channel).connection

        health = RabbitMQHealthIndicator(channel)
    }

    @Test
    fun up() {
        doReturn(properties).whenever(connection).serverProperties

        val result = health.health()

        assertEquals(Status.UP, result.status)
    }

    @Test
    fun down() {
        doThrow(RuntimeException("yo")).whenever(connection).serverProperties

        val result = health.health()

        assertEquals(Status.DOWN, result.status)
    }
}

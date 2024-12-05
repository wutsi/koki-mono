package com.wutsi.koki.event.server.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.wutsi.koki.event.server.service.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.nio.charset.Charset

class RabbitMQEventPublisher(
    private val channel: Channel,
    private val objectMapper: ObjectMapper,

    @Value("\${koki.event-publisher.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.event-publisher.rabbitmq.max-retries}") private val maxRetries: Int,
    @Value("\${koki.event-publisher.rabbitmq.ttl-seconds}") private val ttl: Int
) : EventPublisher {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQEventPublisher::class.java)
    }

    fun processDlq(queue: String, dlq: String) {
        var processed = 0
        var expired = 0
        try {
            while (true) {
                val response = channel.basicGet(dlq, false) ?: break
                val retries: Int = (response.props.headers["x-retries"] as Int?) ?: 0
                if (retries >= maxRetries) {
                    channel.basicReject(response.envelope.deliveryTag, false) // Reject + Drop
                    expired++
                } else {
                    channel.basicPublish(
                        "",
                        queue, // routing-key
                        properties(retries + 1), // basic-properties
                        response.body,
                    )
                    channel.basicAck(response.envelope.deliveryTag, false) // ACK
                    processed++
                }
            }
        } finally {
            LOGGER.info("DLQ=$dlq - $processed message(s) processed, $expired message(s) expired")
        }
    }

    override fun publish(event: Any) {
        val rabbitMQEvent = createRabbitMQEvent(event)
        channel.basicPublish(
            exchangeName,
            "", // routing-key
            properties(), // basic-properties
            objectMapper.writeValueAsString(rabbitMQEvent).toByteArray(Charset.forName("utf-8")),
        )
    }

    private fun createRabbitMQEvent(event: Any): RabbitMQEvent {
        return RabbitMQEvent(
            classname = event::class.java.name,
            payload = objectMapper.writeValueAsString(event),
        )
    }

    private fun properties(retries: Int = 0): BasicProperties {
        return AMQP
            .BasicProperties()
            .builder()
            .headers(
                mapOf(
                    "x-max-retries" to maxRetries,
                    "x-retries" to retries,
                ),
            )
            .expiration((ttl * 1000).toString())
            .contentType("application/json")
            .contentEncoding("utf-8")
            .build()
    }
}

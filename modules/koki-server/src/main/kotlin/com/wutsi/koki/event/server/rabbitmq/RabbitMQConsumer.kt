package com.wutsi.koki.event.server.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLoggerThreadLocal
import org.springframework.context.ApplicationEventPublisher

class RabbitMQConsumer(
    private val objectMapper: ObjectMapper,
    private val applicationEventPublisher: ApplicationEventPublisher,
    channel: Channel,
) : DefaultConsumer(channel) {
    override fun handleDelivery(
        consumerTag: String,
        envelope: Envelope,
        properties: BasicProperties,
        body: ByteArray,
    ) {
        // Add Logger into the ThreadLocal
        val logger = DefaultKVLogger()
        KVLoggerThreadLocal.set(logger)

        logger.add("rabbitmq_consumer_tag", consumerTag)
        try {
            // Read the event...
            val event = objectMapper.readValue(body, RabbitMQEvent::class.java)

            // Process the event
            val payload = extractPayload(event)
            applicationEventPublisher.publishEvent(payload)
            channel.basicAck(envelope.deliveryTag, false)

            logger.add("success", true)
        } catch (ex: Exception) {
            logger.setException(ex)
            logger.add("success", false)

            channel.basicReject(
                envelope.deliveryTag,
                false, // do not requeue - message will go to DLQ
            )
        } finally {
            logger.log()
            KVLoggerThreadLocal.remove() // Clear the tracing context
        }
    }

    private fun extractPayload(event: RabbitMQEvent): Any {
        val clazz = Class.forName(event.classname)
        return objectMapper.readValue(event.payload, clazz)
    }
}

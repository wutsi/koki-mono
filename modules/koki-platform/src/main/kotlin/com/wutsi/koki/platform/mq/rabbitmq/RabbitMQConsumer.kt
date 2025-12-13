package com.wutsi.koki.platform.mq.rabbitmq

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.logger.KVLoggerThreadLocal
import com.wutsi.koki.platform.mq.Consumer
import tools.jackson.databind.json.JsonMapper

class RabbitMQConsumer(
    private val jsonMapper: JsonMapper,
    private val delegate: Consumer,
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

        try {
            // Read the event...
            val event = jsonMapper.readValue(body, RabbitMQEvent::class.java)

            // Process the event
            val payload = extractPayload(event, logger)
            if (delegate.consume(payload)) {
                logger.add("rabbitmq_consumer_tag", consumerTag)
                logger.add("rabbitmq_exchange", envelope.exchange)
                logger.add("rabbitmq_routing_key", envelope.routingKey)
                logger.add("rabbitmq_consumer_class", delegate::class.java.name)
                logger.add("rabbitmq_payload_class", payload::class.java.name)
            }
            channel.basicAck(envelope.deliveryTag, false)
        } catch (ex: Exception) {
            logger.setException(ex)

            channel.basicReject(
                envelope.deliveryTag,
                false, // do not requeue - message will go to DLQ
            )
        } finally {
            logger.log()
            KVLoggerThreadLocal.remove() // Clear the tracing context
        }
    }

    private fun extractPayload(event: RabbitMQEvent, logger: KVLogger): Any {
        val clazz = Class.forName(event.classname)
        return jsonMapper.readValue(event.payload, clazz)
    }
}

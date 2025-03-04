package com.wutsi.koki.platform.mq.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.StorageType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date

class RabbitMQPublisher(
    private val channel: Channel,
    private val objectMapper: ObjectMapper,
    private val storageBuilder: StorageServiceBuilder,

    @Value("\${koki.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.rabbitmq.max-retries}") private val maxRetries: Int,
    @Value("\${koki.rabbitmq.ttl-seconds}") private val ttl: Int,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQPublisher::class.java)
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
                    archive(dlq, response)
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
        try {
            val rabbitMQEvent = createRabbitMQEvent(event)
            channel.basicPublish(
                exchangeName,
                "", // routing-key
                properties(), // basic-properties
                objectMapper.writeValueAsString(rabbitMQEvent).toByteArray(Charset.forName("utf-8")),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unnable to publish event: $event", ex)
        }
    }

    private fun archive(dlq: String, response: GetResponse) {
        val fmt = SimpleDateFormat("yyyy/MM/dd")
        val contentType = response.props.contentType
        val path = "rabbitmq/queues/$dlq/" +
            fmt.format(Date()) +
            "/" +
            response.envelope.deliveryTag +
            extension(contentType)

        try {
            getStorageService().store(
                path = path,
                content = ByteArrayInputStream(response.body),
                contentType = contentType,
                contentLength = response.body.size.toLong()
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to store expired message to $path", ex)
        }
    }

    private fun extension(contentType: String?): String {
        return when (contentType) {
            "application/json" -> ".json"
            else -> ""
        }
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

    private fun getStorageService(): StorageService {
        return storageBuilder.build(StorageType.KOKI, emptyMap())
    }
}

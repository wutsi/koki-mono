package com.wutsi.koki.platform.mq.rabbitmq

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import org.slf4j.LoggerFactory
import tools.jackson.databind.json.JsonMapper
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date

class RabbitMQPublisher(
    private val channel: Channel,
    private val jsonMapper: JsonMapper,
    private val storageBuilder: StorageServiceBuilder,
    private val exchangeName: String,
    private val maxRetries: Int,
    private val ttl: Int,
) : Publisher {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQPublisher::class.java)
    }

    override fun publish(event: Any) {
        try {
            val rabbitMQEvent = createRabbitMQEvent(event)
            channel.basicPublish(
                exchangeName,
                "", // routing-key
                properties(), // basic-properties
                jsonMapper.writeValueAsString(rabbitMQEvent).toByteArray(Charset.forName("utf-8")),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unnable to publish event: $event", ex)
        }
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
            if (processed > 0 || expired > 0) {
                LOGGER.info("DLQ=$dlq - $processed message(s) processed, $expired message(s) expired")
            }
        }
    }

    private fun archive(dlq: String, response: GetResponse) {
        val fmt = SimpleDateFormat("yyyy/MM/dd")
        val contentType = response.props.contentType
        val path =
            "rabbitmq/queues/$dlq/" + fmt.format(Date()) + "/" + response.envelope.deliveryTag + extension(contentType)

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
            payload = jsonMapper.writeValueAsString(event),
        )
    }

    private fun properties(retries: Int = 0): BasicProperties {
        return BasicProperties().builder().headers(
            mapOf(
                "x-max-retries" to maxRetries,
                "x-retries" to retries,
            ),
        ).expiration((ttl * 1000).toString())
            .contentType("application/json")
            .contentEncoding("utf-8")
            .build()
    }

    private fun getStorageService(): StorageService {
        return storageBuilder.default()
    }
}

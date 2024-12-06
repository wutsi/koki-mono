package com.wutsi.koki.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.wutsi.koki.event.server.rabbitmq.RabbitMQEventPublisher
import com.wutsi.koki.event.server.service.EventPublisher
import com.wutsi.koki.platform.storage.StorageService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["koki.event-publisher.type"],
    havingValue = "rabbitmq",
)
class RabbitMQEventPublisherConfiguration(
    private val channel: Channel,
    private val objectMapper: ObjectMapper,
    private val storageService: StorageService,

    @Value("\${koki.event-publisher.rabbitmq.exchange-name}") private val exchangeName: String,
    @Value("\${koki.event-publisher.rabbitmq.max-retries}") private val maxRetries: Int,
    @Value("\${koki.event-publisher.rabbitmq.ttl-seconds}") private val ttl: Int
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQEventPublisherConfiguration::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info("Setting up exchange: $exchangeName")
        channel.exchangeDeclare(
            exchangeName,
            BuiltinExchangeType.FANOUT,
            true, // durable
        )
        LOGGER.info("EventPublisher configured")
    }

    @Bean
    fun eventPublisher(): EventPublisher {
        return RabbitMQEventPublisher(
            channel = channel,
            exchangeName = exchangeName,
            ttl = ttl,
            maxRetries = maxRetries,
            objectMapper = objectMapper,
            storage = storageService,
        )
    }
}

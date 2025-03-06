package com.wutsi.koki.config

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQInitializerConfiguration(
    private val channel: Channel,

    @Value("\${koki.rabbitmq.exchange-name}") private val exchangeName: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RabbitMQInitializerConfiguration::class.java)
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
}
